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

##### Which means does bash offer to combine the coreutils (or any programs for that matter)?

* `|` gives the input of the command on the left to the command on the right;
* `<` and `>` redirect output (overwriting files) - redirecting to `/dev/null` deletes;
* `<(...)` redirects output of the command when using pipe is not possible (process substitution);
* `if [...];` then performs a conditional control;
* ...



#### Exercise 5

##### Write a regular expression that matches all e-mail addresses in the file mails_match.txt and that does not match any of the lines in mail_dont_match.txt.

`[a-zA-z0-9][a-zA-z0-9\-+\.]*[@][a-zA-Z][a-zA-Z]*\.?[a-zA-Z0-9]*\.[a-zA-Z][a-zA-Z]?[a-zA-Z]`



#### Exercise 7

```bash
hex=$1
hex=${hex:1:8} # remove hash
alpha=$((16#${hex:6:2})) # converts hexadecimal to 0-255
LC_NUMERIC=C
printf "rgb(%d,%d,%d,%.2f)\n" 0x${hex:0:2} 0x${hex:2:2} 0x${hex:4:2} $(bc <<< "scale=3;${alpha}/255")
# 0x${hex:0:2} returns first 0x"2 letters", e.g. 0xFF, when passed to %d it gets converted to decimal
# $(...) means execute the command in the parentheses in a subshell and return its stdout.
# <<< The strings is expanded and supplied to the command on its standard input.
```

