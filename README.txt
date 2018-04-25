
项目架构：Spring + SpringMVC + Hibernate + Redis + Canal

注意事项：
    1、连接数据库的属性我和谐掉了，到时候需要配置一下自己的数据库信息。配置文件地址：/src/main/resources/jdbc.properties
    2、由于web.xml配置中，配置SpringMVC时DispatcherServlet拦截了所有的请求，也就是说我们访问静态资源的请求也被
        拦截了（比如说一个页面加载一个js资源）。我在spring-mvc.xml配置有静态资源映射，访问静态资源更方便了
