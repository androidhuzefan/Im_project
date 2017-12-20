package net.hzf.web.italker.push.provider;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.time.LocalDateTime;

/**
 * 用于设置Jersey的Json转换器
 * 用于替换JacksonJsonProvider
 *
 * Json是一种数据格式，便于数据传输、存储、交换
 * 一般的Json都是从网上获取的
 * 当json数据命名和java命名产生不一致时，可以通过注释的方式实现更换名字，更方便进行代码处理
 *
 * Gson是一种组件库，可以把java对象数据转换成json数据格式
 * gson的构造函数提供了解析成数组对象的方法，所以直接像解析单个对象一样使用就行
 * Gson解析注意：
 *  1. 内部嵌套的类必须是static的，要不然解析会出错
 *  2. 类里面的属性名必须跟Json字段里面的Key是一模一样的
 *  3. 内部嵌套的用[]括起来的部分是一个List，所以定义为 public List<B> b，而只用{}嵌套的就定义为 public C c，Type listType = new TypeToken<ArrayList<Foo01>>(){}.getType();
 *  （ Type listType = new TypeToken<ArrayList<Foo01>>(){}.getType()）
 *
 * * JacksonJsonProvider的弊端：
 * 1.布尔变量在输出是开头带有is的布尔变量
 * 2.该解析器对集合是遍历输出，输出流被占满
 * <p>
 * 该工具类完成了，把Http请求中的请求数据转换为Model实体，
 * 同时也实现了把返回的Model实体转换为Json字符串
 * 并输出到Http的返回体中。
 *
 * @param <T> 任意类型范型定义
 */
@Provider
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GsonProvider<T> implements MessageBodyReader<T>, MessageBodyWriter<T> {
    // 共用一个全局的Gson
    private static final Gson gson;

    static {
        //gsonBuilder可以通过检查json数据的格式，将符合用户设置格式的数据变为相应的对象
        //例子：初始化GsonBuilder后，通过setDateFormat来设置什么样的数据会变为date对象，之后再通过gsonBuilder的create来建立gson对象，然后解析即可
        // Gson 初始化
        GsonBuilder builder = new GsonBuilder()
                // 序列化为null的字段
                .serializeNulls()
                // 仅仅处理带有@Expose注解的变量
                .excludeFieldsWithoutExposeAnnotation()
                // 支持Map
                .enableComplexMapKeySerialization();
        // 添加对Java8LocalDateTime时间类型的支持
        //Gson解析器框架默认不对LocalDateTime解析
        builder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeConverter());
        gson = builder.create();
    }

    /**
     * 取得一个全局的Gson
     *
     * @return Gson
     */
    public static Gson getGson() {
        return gson;
    }

    public GsonProvider() {
    }

    @Override
    public boolean isReadable(Class<?> type, Type genericType,
                              Annotation[] annotations, MediaType mediaType) {
        return true;
    }

    /**
     * 把Json的字符串数据, 转换为T类型的实例
     */
    @Override
    public T readFrom(Class<T> type, Type genericType, Annotation[] annotations,
                      MediaType mediaType, MultivaluedMap<String, String> httpHeaders,
                      InputStream entityStream) throws IOException, WebApplicationException {
        try (JsonReader reader = new JsonReader(new InputStreamReader(entityStream, "UTF-8"))) {
            return gson.fromJson(reader, genericType);
        }
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType,
                               Annotation[] annotations, MediaType mediaType) {
        return true;
    }

    @Override
    public long getSize(T t, Class<?> type, Type genericType,
                        Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    /**
     * 把一个T类的实例输出到Http输出流中
     */
    @Override
    public void writeTo(T t, Class<?> type, Type genericType, Annotation[] annotations,
                        MediaType mediaType, MultivaluedMap<String, Object> httpHeaders,
                        OutputStream entityStream) throws IOException, WebApplicationException {
        //TypeAdapter<T> adapter = gson.getAdapter((TypeToken<T>) TypeToken.get(genericType));
        try (JsonWriter jsonWriter = gson.newJsonWriter(new OutputStreamWriter(entityStream, Charset.forName("UTF-8")))) {
            gson.toJson(t, genericType, jsonWriter);
            jsonWriter.close();
        }
    }
}