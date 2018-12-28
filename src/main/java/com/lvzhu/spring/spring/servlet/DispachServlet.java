package com.lvzhu.spring.spring.servlet;

import com.lvzhu.spring.spring.annotation.Autowried;
import com.lvzhu.spring.spring.annotation.Controller;
import com.lvzhu.spring.spring.annotation.RequestMapping;
import com.lvzhu.spring.spring.annotation.Service;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lijun
 * @email 1796518311@qq.com
 * @since 2018-12-27 0:04
 */
@WebServlet(name = "lvzhumvc", urlPatterns = "/*", loadOnStartup = 0,
        initParams = {@WebInitParam(name = "contextConfigLocation", value = "classpath:application.properties")})
public class DispachServlet extends HttpServlet {
    private Properties contextConfig = new Properties();

    /**
     * ClassNames
     */
    private List<String> classNames = new ArrayList<>();

    /**
     * beanMap
     */
    private Map<String, Object> beanMap = new ConcurrentHashMap<String, Object>();

    /**
     * handlerMapping
     */
    private Map<String, Method> handlerMapping = new ConcurrentHashMap<String, Method>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            doDisPatch(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().write("500 Exception" + Arrays.toString(e.getStackTrace()));
        }

    }

    private void doDisPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (handlerMapping.isEmpty()) {
            return;
        }
        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replace(contextPath, "").replace("/+", "/");
        if (!this.handlerMapping.containsKey(url)) {
            resp.getWriter().write("404 Not Found!");
            return;
        }
        Method method = this.handlerMapping.get(url);
        System.out.println(method);
    }

    @Override
    public void init(ServletConfig config) throws ServletException {

        //开始初始化的进程
        String contextConfigLocationURL = config.getInitParameter("contextConfigLocation");
        //定位
        doLoadConfig(contextConfigLocationURL);
        //加载
        doScanner(contextConfig.getProperty("scanPackage"));
        //注册
        doRegistry();
        //自动依赖注入
        //在Spring中是通过调用getBean方法才出发依赖注入的
        doAutowired();

        //将@RequestMapping中配置的url和一个Method关联上
        //以便于从浏览器获得用户输入的url以后，能够找到具体执行的Method通过反射去调用
        initHandlerMapping();

    }

    private void initHandlerMapping() {
        if (beanMap.isEmpty()) {
            return;
        }
        for (Map.Entry<String, Object> entry : beanMap.entrySet()) {
            Class<?> clazz = entry.getValue().getClass();
            if (!clazz.isAnnotationPresent(Controller.class)) {
                continue;
            }
            String baseUrl = "";
            if (clazz.isAnnotationPresent(RequestMapping.class)) {
                RequestMapping requestMapping = clazz.getAnnotation(RequestMapping.class);
                baseUrl = requestMapping.value();
            }
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                if (!method.isAnnotationPresent(RequestMapping.class)) {
                    continue;
                }
                RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                String url = requestMapping.value();
                url = (baseUrl + "/" + url).replaceAll("/+", "/");

                handlerMapping.put(url, method);

                System.out.println("Mappping:" + url + "," + method);
            }

        }
    }

    private void doAutowired() {
        if (beanMap.isEmpty()) {
            return;
        }
        for (Map.Entry<String, Object> entry : beanMap.entrySet()) {
            //扫描类所有的属性，看看有没有加@AutoWrited
            Field[] fields = entry.getValue().getClass().getDeclaredFields();
            for (Field field : fields) {
                if (!field.isAnnotationPresent(Autowried.class)) {
                    continue;
                }
                Autowried autowried = field.getAnnotation(Autowried.class);
                String beanName = autowried.value();
                if ("".equals(beanName)) {
                    beanName = field.getType().getName();
                }
                field.setAccessible(true);
                try {
                    field.set(entry.getValue(), beanMap.get(beanName));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    /**
     * 实例化所有扫描到的类 并且把他放到容器中（即SpringIOC容器中）
     */
    private void doRegistry() {
        if (classNames.isEmpty()) {
            return;
        }
        for (String className : classNames) {
            try {
                Class<?> clazz = Class.forName(className);
                if (clazz.isAnnotationPresent(Controller.class)) {
                    String beanName = lowerFirstCase(clazz.getSimpleName());
                    beanMap.put(beanName, clazz.newInstance());
                    System.out.println("doRegistry beanName :"+beanName);
                } else if (clazz.isAnnotationPresent(Service.class)) {
                    //1.如果自己设置了一个名字，就要用自己的名字
                    //2.如果自己没有设置，默认首字母小写
                    //3.如果@Autowried的是一个接口，默认要将其实现类注入进来
                    Service service = clazz.getAnnotation(Service.class);
                    String beanName = service.value();

                    if ("".equals(beanName)) {
                        beanName = lowerFirstCase(clazz.getSimpleName());
                    }
                    Object instance = clazz.newInstance();
                    System.out.println("doRegistry beanName :"+beanName);
                    beanMap.put(beanName, instance);

                    Class<?>[] interfaces = clazz.getInterfaces();
                    for (Class<?> i : interfaces) {
                        beanMap.put(i.getName(), instance);
                        System.out.println("doRegistry interface beanName :"+i.getName());
                    }

                } else {
                    continue;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 根据配置文件提供的需要扫描的包
     * 递归出所有需要实例话类名字
     *
     * @param scanPackageName 要扫描的包
     */
    private void doScanner(String scanPackageName) {
        String urlPath = scanPackageName.replaceAll("\\.", "/");
        URL url = this.getClass().getClassLoader().getResource("/" + urlPath);
        File dir = new File(url.getFile());
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                doScanner(scanPackageName + "." + file.getName());
            } else {
                String className = scanPackageName + "." + file.getName().replace(".class", "");
                System.out.println("doScanner className :" + className);
                classNames.add(className);
            }
        }
    }

    /***
     *  加载配置文件到contextConfig
     * @param contextConfigLocationURL
     */
    private void doLoadConfig(String contextConfigLocationURL) {
        System.out.println("配置文件路径：" + contextConfigLocationURL);
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(contextConfigLocationURL.replace("classpath:", ""));
        try {
            contextConfig.load(resourceAsStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != resourceAsStream) {
                    resourceAsStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 字符串首字母小写
     *
     * @param str 要转换的字符串
     * @return 新的首字母小写的字符串
     */
    private String lowerFirstCase(String str) {
        char[] chars = str.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }
}
