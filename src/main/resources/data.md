Notes for regenerating data

2-9 letters
  copy words from http://www.allscrabblewords.com/2-letter-words/ into  words2.txt

  perl -pi -e 's/ /\n/g' words2.txt; cat words2.txt | tr '[a-z]' '[A-Z]' > wordlist/2.txt

10-15 letters:
  curl http://www.scrabbleplayers.org/words/10-15-20030401.txt > words1X.txt

  ./generate-wordlist.sh
