#!/bin/bash
input="wordlist.txt"

outdir="wordlist"
[ ! -d "$outdir" ] &&  $_mkdir -p "$outdir"

for i in {2..15..1}
  do 
     echo > "${outdir}/${i}.txt"
 done

while IFS= read -r line
do
  length=${#line}
  echo "$line" >> "${outdir}/${i}.txt"
done < "$input"
