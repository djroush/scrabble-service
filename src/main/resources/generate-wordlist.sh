#!/bin/bash
input="words1X.txt"
outdir="wordlist"

[ ! -d "$outdir" ] &&  mkdir -p "$outdir"
rm -rf "${outdir}/*.txt"

while IFS= read -r line
do
  length=${#line} 
  echo "$line" >> "${outdir}/${length}.txt" 
done < "$input"
