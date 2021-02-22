//
// Created by Andrew Rayskiy on 31.01.21.
//

#include "JoinTree.h"

#include <iostream>
#include <numeric>


std::string JoinTreeNode::Visualize() const {
    const auto [width, compact] = VisualizeCompact();
    std::string result;
    for (uint32_t chunk = 0; chunk < compact.size() / width; chunk++) {
        result += compact.substr(chunk * width, width) + '\n';
    }
    return result;
}


JoinTreeLeafNode::JoinTreeLeafNode(std::string name, double size) : JoinTreeNode(size), Name(std::move(name)) {}


JoinTreeInnerNode::JoinTreeInnerNode(std::unique_ptr <JoinTreeNode> left, std::unique_ptr <JoinTreeNode> right,
                                     const TSelectivityMap &selectivityMap) : Left(std::move(left)), Right(std::move(right)) {
    Cost += Left->GetCost() + Right->GetCost();

    double selectivity = 1;
    for (const auto& leftSubtreeNode: Left->GetSubtree()) {
        for (const auto& rightSubtreeNode: Right->GetSubtree()) {
            auto itr = selectivityMap.find(std::minmax(leftSubtreeNode->GetName(), rightSubtreeNode->GetName()));
            if (itr == selectivityMap.end()) {
                continue;
            }
            selectivity *= itr->second;
        }
    }

    Size = Left->GetSize() * Right->GetSize() * selectivity;

    Cost += Size;
}

std::vector<const JoinTreeLeafNode*> JoinTreeInnerNode::GetSubtree() const {
    auto left = Left->GetSubtree(), right = Right->GetSubtree();
    std::move(right.begin(), right.end(), std::back_inserter(left));
    return left;
}

std::pair<uint32_t, std::string> JoinTreeInnerNode::VisualizeCompact() const {
    auto [lw, ls] = Left->VisualizeCompact();
    auto [rw, rs] = Right->VisualizeCompact();

    //std::cerr << lw << " " << rw << std::endl;
    //std::cerr << ls << " & " << rs << std::endl;
    //std::cerr << std::endl;

    // at least 2 back/forward slashes (satisfied)

    int idx = 0;

    auto fillSpaces = [](const uint32_t length) -> std::string {
        std::string result;
        for (auto i = 0; i < length; i++) result.push_back(' ');
        return result;
    };

    const auto w = std::max(lw, rw);

    auto result = fillSpaces(w) + 'x' + fillSpaces(w);

    const auto lSlash = (w + 1) / 2, rSlash = (w + 1) / 2;

    uint32_t lChunk = 0, rChunk = 0;

    while (lChunk * lw < ls.size() || rChunk * rw < rs.size()) {
        std::string curr;
        if (idx < lSlash) {
            curr += fillSpaces(w - idx - 1);
            curr += '/';
            curr += fillSpaces(idx);
        } else {
            if (lChunk * lw < ls.size()) {
                curr += fillSpaces((w - lw) / 2) + ls.substr(lChunk * lw, lw) + fillSpaces(w - lw - (w - lw) / 2);
                lChunk++;
            } else {
                curr += fillSpaces(w);
            }
        }
        curr += ' ';
        if (idx < rSlash) {
            curr += fillSpaces(idx);
            curr += '\\';
            curr += fillSpaces(w - idx - 1);
        } else {
            if (rChunk * rw < rs.size()) {
                curr += fillSpaces((w - rw) / 2) + rs.substr(rChunk * rw, rw) + fillSpaces(w - rw - (w - rw) / 2);
                rChunk++;
            } else {
                curr += fillSpaces(w);
            }
        }
        idx++;
        //std::cerr << "LC: " << lChunk << ", RC: " << rChunk << " " << "Curr step: " << curr << std::endl;
        result += curr;
    }
    return {2 * w + 1, result};
}

std::unique_ptr<JoinTree> JoinTree::Create(
        const std::vector<bool>& traversal,
        const std::vector<std::string>& nodeOrder,
        const TNodes& nodes,
        const TSelectivityMap& selectivityMap) {
    uint32_t nextI = 0;
    auto next = [&]() -> bool {
        if (nextI >= traversal.size()) { return false; }
        return traversal[nextI++];
    };

    uint32_t leafI = 0;
    auto leaf = [&]() -> std::unique_ptr<JoinTreeLeafNode> {
        const auto name = nodeOrder[leafI++];
        return std::make_unique<JoinTreeLeafNode>(name, nodes.at(name));
    };

    std::function<std::unique_ptr<JoinTreeNode>()> builder = [&]() -> std::unique_ptr<JoinTreeNode> {
        auto process = [&]() -> std::unique_ptr<JoinTreeNode> {
            if (next()) {
                return builder();
            } else {
                return leaf();
            }
        };
        auto left = process(), right = process();
        return std::make_unique<JoinTreeInnerNode>(std::move(left), std::move(right), selectivityMap);
    };

    next();
    return std::make_unique<JoinTree>(builder());
}

const JoinTreeNode* JoinTree::GetRoot() const { return Root.get(); }
