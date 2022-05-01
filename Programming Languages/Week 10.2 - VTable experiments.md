Layout: (according to slides)
![[virtual-baseclass-vtables.png]]

Emitted vtable:
```
Vtable for 'C' (17 entries).
   0 | vbase_offset (32)
   1 | offset_to_top (0)
   2 | C RTTI
       -- (A, 0) vtable address --
       -- (C, 0) vtable address --
   3 | int A::f(int)
   4 | int C::h(int)
   5 | vbase_offset (16)
   6 | offset_to_top (-16)
   7 | C RTTI
       -- (B, 16) vtable address --
   8 | int B::g(int)
   9 | vcall_offset (-32)
  10 | vcall_offset (-16)
  11 | vcall_offset (-32)
  12 | offset_to_top (-32)
  13 | C RTTI
       -- (W, 32) vtable address --
  14 | int A::f(int)
       [this adjustment: 0 non-virtual, -24 vcall offset offset]
  15 | int B::g(int)
       [this adjustment: 0 non-virtual, -32 vcall offset offset]
  16 | int C::h(int)
       [this adjustment: 0 non-virtual, -40 vcall offset offset]
	   
Virtual base offset offsets for 'C' (1 entry).
   W | -24

Thunks for 'int C::h(int)' (1 entry).
   0 | this adjustment: 0 non-virtual, -40 vcall offset offset

VTable indices for 'C' (1 entries).
   1 | int C::h(int)
```

Corresponding output by the program:
```
DeltaW         32
DeltaA         0
RTTI           55ae13efcd68
A::f           55ae13efa2a0
C::h           55ae13efa380
DeltaW-DeltaB  16
DeltaB         -16
RTTI           55ae13efcd68
B::g           55ae13efa310
?              -32
?              -16
?              -32
DeltaW         -32
RTTI           55ae13efcd68
A::Wf          55ae13efa2e0
B::Wg          55ae13efa350
C::Wh          55ae13efa3c0
```
