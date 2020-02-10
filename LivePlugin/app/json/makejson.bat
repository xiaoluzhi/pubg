@echo on

@rem 编译pb文件并且同步到目录

@rem 删除上次残留
@rd /q/s src

@java -classpath json2java.jar com.tencent.tga.gson.jsonUitl.JsonUtils


@xcopy /E/Y  .\src  ..\src\main\java

@rd /q/s src

@echo completed
@pause