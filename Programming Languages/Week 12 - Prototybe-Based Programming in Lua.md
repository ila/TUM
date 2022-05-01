# Prototype-Based Programming in Lua
Core idea: "why bother modeling types for my quick hack?"

Our example prototype-based language: *Lua*.

## Basic Language Features
- implicitely defined global variables
- no type annotations: dynamic typing
- semicolons, or even new lines to separate statements, can also be left out

```lua
--[[set some
variables
--]]
s = 0
p = s+1 p = s+3 -- multiple statements in one line allowed
```

### Types in Lua
Every value carries a type that can be retrieved with the `type` function.

```lua
type(true)         -- boolean
type(42)           -- number
type("Forty-Two")  -- string
type(type)         -- function
type(nil)          -- nil
type({})           -- table
-- two more basic types: `userdata` (arbitrary C data) and `thread`
```

Strings can be concatted:
```lua
s = "A " .. 1 .. " B " .. 2 -- "A 1 B 2"
```

Using a string as a number parses it:
```lua
a = "42" + "1"  -- 43
b = "42" + 1    -- 43
```

Types associated with a variable name are not static:
```lua
a = 1
type(a)   -- number
a = true
type(a)   -- boolean
```

All unitialized variables have the type `nil`:
```lua
type(some_uninit_variable) -- nil
```

### Functions
Functions are first-class citizens, i.e. can be stored into variables, etc.

Syntax:
```lua
-- <--> python lambda expression style
fun1 = function(a, b)
	return a + b
end

-- syntactic sugar: <--> python def: style 
function fun2(a, b)
	return a * b
end

-- syntactic sugar: associate function with a table
t = {}
function t.fun3(a, b)
	return a / b
end

fun1(3, 2) -- 5
fun2(3, 2) -- 6
t.fun3(3, 2) -- 1.5
```

### Tables
The only basic complex datatype: the *table*, which stores key - value pairs. (Much like a python `dict`, or maybe rather a python `SimpleNamespace` since dot indexing is supported).

Keys can be arbitrary values expect `nil`.

Syntax:
```lua
a = {}
n = "name1"

-- equivalent ways to access table entries
a["name1"] = 1
a[n] = 1
a.name1 = 1

-- unitialized entries are nil:
print(t.name2) -- nil

-- delete an entry by setting it to nil
t.name1 = nil
```

Table constructors have the syntax:
```lua
a = {name1=2, name2=5}
```

Table variables have reference semantics:
```lua
a = {name1=2}
b = a
b.name1 = 100
print(a.name1) -- 100
```

## Metatables
### Attaching Behavior to Tables
A *metatable* is an ordinary table which usually contains implementations of functions with certain naming conventions. A table can carry a metatable, with the effect that some methods implemented in the metatable are used in the context of that table.

```lua
meta = {}
function meta.__tostring(person)
	return person.prefix .. " " .. person.name
end
a = {prefix="Dr.", name="Petter"}
setmetatable(a, meta) -- meta is now the metatable attached to a
print(a)			  --[[ print uses the __tostring() function from the 
						    metatable and prints "Dr. Petter" --]]
```

The metatable can be get and set via
```lua
setmetatable(a, meta)
getmetatable(a)
```

Among the functions with naming conventions that the metatable can override are
- operators like `__add`, `__mul`, `__sub`, `__div`, `__pow`, `__concat`, `__unm`
- comparators like `__eq`, `__lt`, `__le`

These methods are called *metamethods*.

### Delegation
##### The `__index` method
Delegation is the core concept of prototype-based programming.

*Failed name lookups* can be handled dynamically via the `__index(tbl, key)` method of the metatable. For example: delegate the failed lookups to a *prototype*, i.e. a table with default values.

Example:
```lua
meta = {}
function meta.__tostring(person)
	return person.prefix .. " " .. person.name
end
function meta.__index(tbl, key)
	return tbl.prototype[key]			  -- delegate lookup to a prototype
end

job = { prefix="Dr." }
person = { name="Petter", prototype=job } -- use `job` as the prototybe
setmetatable(person, meta)
print(person) 							  -- "Dr. Petter"
```

#####  `__index` as table
For convenience, `__index` can be specified as table instead of as method. In this case, every failed name lookup is looked up directly in that table.

```lua
-- ...
job = { prefix = "Dr." }
meta.__index = job
setmetatable(person, meta)
-- ...
```

![[prototype-example-person-job.png]]


##### The `__newindex` method
The `__newindex(tbl, key, val)` method allows to hook into unresolved table value *updates*.

Example:
```lua
-- ...
function meta.__newindex(tbl, key, val)
	if (key == "title" and tbl.name == "Guttenberg") then
		error("No title for you!")
	else
		-- have to keep an internal `data` table to avoid infinite recursion
		tbl.data[key] = val
	end
end

person = { data = {} }
meta.__index = person.data
setmetatable(person, meta)

person.name = "Guttenberg" -- this works
person.name = "Dr."		   -- this fails!
```

Here, the `__newindex()` method is used to somewhat protect an object (not a very good protection, of course).

There also exists a method to hook into modifying existing fields.

## Mimicking Object Oriented Programming
### Mimicking Classes and Objects
We can mimick "classes" and "objects" by creating a table $A$ that represents a class and tables $a, b, c, \dots$ that have $A$ as metatable, where `__index()` is delegated to $A$.

```lua
Account = {}
function Account.withdraw(acc, val)
	-- `acc` is used like a `this` reference here
	acc.balance = acc.balance - val
end

-- class/object setup
Account.__index = Account
mikes = { balance = 0 }
daves = { balance = 0 }
setmetatable(mikes, Account)
setmetatable(daves, Account)

-- using a "class" function: multiple ways to do the same thing
Account.withdraw(mikes, 10)
mikes.withdraw(mikes, 10) -- redundant "mikes"
daves.withdraw(mikes, 10) -- also withdraws from `mikes`
mikes:withdraw(10) -- better syntax: syntactic sugar for receiver param
```

### Mimicking Constructors and Instance Methods
The previous example can be refined by
- providing a constructor
- rewriting `withdraw` with syntactic sugar for the receiver parameter

```lua
Account = {}
function Account:withdraw(val) -- the : syntax also works here
	self.balance = self.balance - val
end
function Account:new(template)
	-- if called like Account:new(..), `self` is the Account table
	template = template or {balance = 0} -- providing a default value
	
	-- set up the metatable to index into Account (to connect to withdraw method)
	setmetatable(template, {__index=self})
	return template
end

-- called with : syntax: hence the `self` is the Account table
giro1 = Account:new({balance=10})
giro1:withdraw(10)

 -- without passing the template parameter
giro2 = Account:new()
giro2:withdraw(10)
```

### Mimicking Inheritance
Inheritance can be implemented by setting delegating via `__index` from the "subclass" table to the "superclass" table.
```lua
LimitedAccount = {}
setmetatable(LimitedAccount, {__index=Account})
function LimitedAccount:new()
	instance = { balance=0, limit=100 }
	setmetatable(instance, {__index=self})
	return instance
end
function LimitedAccount:withdraw(val)
	-- implement a different `withdraw()` for `LimitedAccount`
end
```

### Mimicking Multi-Inheritance
We can create our own model of multi-inheritance: for example, one where a class can have two parents and conflicts are resolved by giving the first parent class precedence.

```lua
function createClass(parent1, parent2) -- parents = "class-like tables"
	local c = {} -- the new child class
	setmetatable(c, {__index = 
		function (t, k)
			local v = parent1[k]
			if (v != nil) then return v end
			return parent2[k]
		end
	})
	c.__index = c -- relevant for instances of c
	function c:new (o)
		o = o or {}
		setmetatable(o, c)
		return o
	end
	return c
end
```

## Implementation of Lua and Further Topics
Implementation-wise, objects are represented by a type id and a value; values are just unions of the different low-level types.
```C
typedef struct {
	int type_id;
	Value v;
} TObject;

typedef union {
	void *p;
	int b;
	lua_number n;
	GCObject *gc;
} Value;
```

Some optimization can be done for tables: these fork into
- a Hashmap (for dict-like indexing)
- and an integer-indexed array

Further Lua features are coroutines and closures.

## Summary: Prototype-Based Programming
Lessons learned:
- we can possibly ease up the development by abandoning fixed inheritance (but can also have horrible runtime errors because of the dynamic nature of the language)
- OOP and MI can be implemented as special cases of delegation
- The minimal featureset of Lua makes it easy to implement a compiler/interpreter
- To find bugs before runtime, one could try to use some static analysis