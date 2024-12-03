@rem INPUT_FILE - входной файл с текстом. Название файла без кавычек, пробелов, латиницей
@rem OUTPUT_RESULT_RELATiONS - путь до файла, в котором будет записан результат со связями.
@rem OUTPUT_SYNTAGMATIC_LINKS - путь до файла, в который будет записан результат с синтагматическими ссылками.
@rem OUTPUT_EXCEL_REPORT - путь до файла (Excel .xlsx), в который будет записана информация об абсолютных значениях и относительных величинах МинТеМ лексики, а также синтагматические веса.

@set INPUT_FILE=in.txt
@set OUTPUT_RESULT_RELATiONS=out_r.txt
@set OUTPUT_SYNTAGMATIC_LINKS=out_s.txt
@set OUTPUT_EXCEL_REPORT=report.xlsx

@rem запуск основной работы
@cd %~dp0
@echo Для работы нужна установленная Java версии 17+
@java -Dtext-file-path=%INPUT_FILE% -Doutput-relationship-file-path=%OUTPUT_RESULT_RELATiONS% -Doutput-syntagmatic-links-file-path=%OUTPUT_SYNTAGMATIC_LINKS% -Doutput-table-file-path=%OUTPUT_EXCEL_REPORT% -jar syntagmatic-analyser-1.1.0-jar-with-dependencies.jar
