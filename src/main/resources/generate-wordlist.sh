#!/bin/bash

#empty_directories() {}
#
#empty_directory() {
# if else here
#}

#write_files_by_length() {
#}

#sort_anagrams() {
#some perl chomp stuff here
#}

write_anagrams() {
  for sorted_file in $1/3*
  do
    sorted_filename=$(echo $sorted_file | cut -d '/' -f 2)
    length=$(echo $sorted_filename | cut -d '.' -f 1)
    anagram_file=$(echo $2/${length}.json)

    echo '{' > $anagram_file
    prev_anagram=""
    word_list=()
    while IFS= read -r line
    do
      anagram=$(echo $line | cut -d ' ' -f 1)
      word=$(echo $line | cut -d ' ' -f 2)
      echo "anagram: $anagram   word: $word   word_list=${word_list[*]}   prev_anagram: $prev_anagram  "

      if [ "$anagram" = "$prev_anagram" ]
      then 
        word_list+=" \"$word\"";
      elif [ "" !=  "$prev_anagram" ] 
      then
        words=$(echo ${word_list[*]} | sed 's/ /,/g')
        echo "\"$prev_anagram\": [${words}]," >> $anagram_file
        word_list=(\"$word\");
      else 
        word_list=(\"$word\");
      fi
      prev_anagram=$anagram
    done < $sorted_file

    words=$(echo ${word_list[*]} | sed 's/ /,/g')
    echo "\"$prev_anagram\": [${words}]" >> $anagram_file
    echo '}' >> $anagram_file
  done
}

input_file="nwsl2020.txt"
sorted_file="words.txt"
word_dir="wordList"
temp_dir="tempList"
anagram_dir="anagramList"

#empty_directories()

write_anagrams ${temp_dir} ${anagram_dir}

