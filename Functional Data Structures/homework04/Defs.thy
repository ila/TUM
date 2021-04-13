theory Defs
  imports Main
begin

datatype 'a mtree = Leaf | Node (left: "'a mtree") (minimum: 'a) (element: 'a) (right: "'a mtree")

end
  

