

function validated = testmatlabop(multiline_commandstr, desired, prefix, suffix)
formatSpec = "%s is: %s  : %s";
validated = cell(1, numel(multiline_commandstr));
for ln_no = 1:numel(multiline_commandstr)
    try
        new = evalin('base',  append(prefix, multiline_commandstr{ln_no}, suffix));
        if size(desired) == size(new)
            valid = mat2str(abs(new-desired)<.01);
        else
            valid = "wrong result size";
        end
    catch ME
        valid = "false";
        switch ME.identifier
            case 'MATLAB:catenate:dimensionMismatch'
                new = "invalid matlab: size"
            case 'MATLAB:m_illegal_character'
                new = "WARNING check characters";
        
            case 'MATLAB:UndefinedFunction'
                new = "WARNING check definitions";
            otherwise
                new = "general matlab invalid";
                valid = "false";
        end
    end
    validated{ln_no} = sprintf(formatSpec,multiline_commandstr{ln_no}, valid, mat2str(new));
end
end