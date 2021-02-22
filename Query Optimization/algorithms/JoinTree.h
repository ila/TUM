//
// Created by Andrew Rayskiy on 31.01.21.
//

#pragma once

#include <algorithm>
#include <map>
#include <memory>
#include <vector>
#include <string>


using TNodes = std::map<std::string, double>;
using TSelectivityMap = std::map<std::pair<std::string, std::string>, double>;


class JoinTreeLeafNode;

class JoinTreeNode {
public:
    JoinTreeNode() = default;
    JoinTreeNode(double size) : Size(size) {}

    virtual double GetCost() const = 0;
    virtual std::pair<uint32_t, std::string> VisualizeCompact() const = 0;

    virtual std::string Visualize() const;
    virtual std::vector<const JoinTreeLeafNode*> GetSubtree() const = 0;

    double GetSize() const { return Size; }

    virtual ~JoinTreeNode() = default;

protected:
    double Size = 0;
};


class JoinTreeLeafNode: public JoinTreeNode {
public:
    JoinTreeLeafNode(std::string name, double size);

    const std::string& GetName() const { return Name; }

    double GetCost() const override { return 0; }
    std::pair<uint32_t, std::string> VisualizeCompact() const override { return {Name.size(), Name}; }
    std::vector<const JoinTreeLeafNode*> GetSubtree() const override { return {this}; }

private:
    std::string Name;
};


class JoinTreeInnerNode: public JoinTreeNode {
public:
    JoinTreeInnerNode(std::unique_ptr<JoinTreeNode> left, std::unique_ptr<JoinTreeNode> right, const TSelectivityMap& selectivityMap);

    double GetCost() const override { return Cost; }
    std::pair<uint32_t, std::string> VisualizeCompact() const override;
    std::vector<const JoinTreeLeafNode*> GetSubtree() const override;

private:
    std::unique_ptr<JoinTreeNode> Left = nullptr, Right = nullptr;
    double Cost = 0;
};


class JoinTree {
public:
    static std::unique_ptr<JoinTree> Create(
            const std::vector<bool>& traversal,
            const std::vector<std::string>& nodeOrder,
            const TNodes& nodes,
            const TSelectivityMap& selectivityMap);

    const JoinTreeNode* GetRoot() const;

public:
    // bug!
    JoinTree(std::unique_ptr<JoinTreeNode> root) : Root(std::move(root)) {}

private:
    std::unique_ptr<JoinTreeNode> Root;
};
