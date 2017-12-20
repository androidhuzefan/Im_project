package net.hzf.web.italker.push;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import net.hzf.web.italker.push.provider.GsonProvider;
import net.hzf.web.italker.push.service.AccountService;
import org.glassfish.jersey.server.ResourceConfig;

import java.util.logging.Logger;

public class Application extends ResourceConfig {
    public Application(){
        // 注册逻辑处理的包名
        //packages("net.hzf.web.italker.push.service");
        packages(AccountService.class.getPackage().getName());

        // 注册Json解析器
//        register(JacksonJsonProvider.class);
        //替换解析器为Gson
        //JacksonJsonProvider的弊端：
            //1.布尔变量在输出是开头带有is的布尔变量
                //2.该解析器对集合是遍历输出，输出流被占满
        register(GsonProvider.class);

        // 注册日志打印输出
        register(Logger.class);

    }
}

