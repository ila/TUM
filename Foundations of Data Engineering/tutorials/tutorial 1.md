## Foundations in Data Engineering - tutorial 1

#### Exercise 1 

##### Which tools are there in GNU coreutils and for what are they useful? 

* `cat` copies a file to the standard output;
* `man` shows the manual for a command;
* `ls` lists the files in a directory;
* `file` gives the file type;
* `rm` removes a file or a directory;
* `cd` changes directory;
* `grep` searches for patterns;
* `less` and more allow pagination of output;
* `head` and `tail` allow to see top or bottom lines;
* `shuf` makes a random permutation of lines;
* `wc` performs word count;
* ...



#### Exercise 2

Which means does bash offer to combine the coreutils (or any programs for that matter)?

* `|` gives the input of the command on the left to the command on the right;
* `<` and `>` redirect output (overwriting files) - redirecting to `/dev/null` deletes;
* `<(...)` redirects output of the command when using pipe is not possible;
* `if [...];` then performs a conditional control;
* 