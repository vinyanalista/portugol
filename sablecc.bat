@echo off
:: http://stackoverflow.com/questions/6836566/batch-file-delete-all-files-and-folders-in-a-directory
rd "sablecc/br" /s /q
call java -cp lib/sablecc.jar org.sablecc.sablecc.SableCC src/portugol.sablecc -d sablecc
pause