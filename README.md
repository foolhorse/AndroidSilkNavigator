# AndroidSilkNavigator

** UNDER CONSTRUCTION **

SilkNavigator is a simple page navigate tool based on annotation , you can add interceptor also.

SilkNavigator is written in kotlin.

## Usage

### Dependencies

```groovy


```

### Module name

Set module name in module's build.gradle

```groovy
kapt {
    arguments {
        arg("moduleName", "user")
    }
}
```

### Init

call `init` before using Navigator. for example `Application`'s `onCreate`, and pass module name to it.
```
Navigator.init(listOf("app","user"))
```

### Page navigate

```Java
@Route("login")
public class LoginActivity extends AppCompatActivity {
    // ...
}
```

```kotlin
Navigator.getInstance().from(this).to("login").go()
```

### Interceptor

just add an Interceptor object , Or lambda 😎。

```kotlin
Navigator.getInstance().addInterceptor { source, destination ->
    if (!isLogin && "user/edit" == destination) {
        Toast.makeText(source, "need login", Toast.LENGTH_SHORT).show()
        Navigator.getInstance().from(source).to("login").go()
        true
    } else {
        false
    }
}
```
