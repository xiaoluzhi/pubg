@echo on

@rem 编译pb文件并且同步到目录

@rem 删除上次残留
@rd /q/s src

@java -jar -Dfile.encoding=UTF-8 wire/wire-compiler-1.6.1-jar-with-dependencies.jar --proto_path=.\ --java_out=src *.proto

@rd /q/s ..\src\main\java\com\tencent\protocol

@md ..\src\main\java\com\tencent\protocol

@xcopy /E  src\com\tencent\protocol  ..\src\main\java\com\tencent\protocol

@rd /q/s src

@echo completed
@pause