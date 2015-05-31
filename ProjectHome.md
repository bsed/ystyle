适合中小型开发的mvc框架，实现跳转常用配置，属性自动注入，提供属性转换接口和各种注解功能，比如
> @Autowired:自动注入
> @Proxy:代理注解，代理工厂只需实现ProxyFactory接口，或直接集成DefaultProxyFactory，支持无限代理，扩展极为方便
> @service：service层注解，配套的有@Transactional注解，自动事务处理
> @SingleTon:单例注解
> @UploadFile:上传注解，支持自动上传并返回文件各种信息，只需要配置上传路径，或从上下文资源动态获取路径变量。
> DB连接：目前支持c3p0,proxool，阿里巴巴Druid,自定义连接池
> DB操作：封装dbutils,暴露Session,Transaction等常用接口，使用方便