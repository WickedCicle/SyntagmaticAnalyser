@rem INPUT_FILE - входной файл с текстом. Название файла без кавычек, пробелов, латиницей
@rem OUTPUT_RESULT_RELATiONS - путь до файла, в котором будет записан результат со связями.
@rem OUTPUT_SYNTAGMATIC_LINKS - путь до файла, в который будет записан результат с синтагматическими ссылками.
@rem OUTPUT_EXCEL_REPORT - путь до файла (Excel .xlsx), в который будет записана информация об абсолютных значениях и относительных величинах МинТеМ лексики, а также синтагматические веса.
@rem ALLOWED_TYPE_OF_SPEECH_RESULT - путь до файла, в котором будут указаны части речи, которые будут попадать в указанные выше два файла. Указывается числовое значение (из JMorfSdk) части речи, каждая часть речи с новой строки. По умолчанию (если файл не указан) в результат попадают все части речи (включая служебные).

@set INPUT_FILE=in.txt
@set OUTPUT_RESULT_RELATiONS=out_r.txt
@set OUTPUT_SYNTAGMATIC_LINKS=out_s.txt
@set OUTPUT_EXCEL_REPORT=report.xlsx
@set ALLOWED_TYPE_OF_SPEECH_RESULT=

@rem запуск основной работы
@cd %~dp0
@echo Для работы нужна установленная Java версии 17+
@java -Dtext-file-path=%INPUT_FILE% -Doutput-relationship-file-path=%OUTPUT_RESULT_RELATiONS% -Doutput-syntagmatic-links-file-path=%OUTPUT_SYNTAGMATIC_LINKS% -Doutput-table-file-path=%OUTPUT_EXCEL_REPORT% -Dallowed-type-of-speech-for-result-file-path=%ALLOWED_TYPE_OF_SPEECH_RESULT% -jar syntagmatic-analyser-1.1.0-jar-with-dependencies.jar