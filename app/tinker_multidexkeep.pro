
#tinker multidex keep patterns:
-keep public class * implements com.tencent.tinker.entry.ApplicationLifeCycle {
    <init>(...);
    void onBaseContextAttached(android.content.Context);
}

-keep public class com.tencent.tinker.entry.TinkerApplicationInlineFence {
    <init>(...);
    void attachBaseContext(com.tencent.tinker.loader.app.TinkerApplication, android.content.Context);
}

-keep public class * implements com.tencent.tinker.loader.app.ITinkerInlineFenceBridge {
    <init>(...);
    void attachBaseContext(com.tencent.tinker.loader.app.TinkerApplication, android.content.Context);
}

-keep public class * extends com.tencent.tinker.loader.TinkerLoader {
    <init>(...);
}

-keep class com.tencent.tinker.loader.TinkerTestAndroidNClassLoader {
    <init>();
}

-keep public class * extends android.app.Application {
     <init>();
     void attachBaseContext(android.content.Context);
}

-keep class com.tencent.tinker.loader.TinkerTestAndroidNClassLoader {
    <init>(...);
}

#your dex.loader patterns here
-keep class com.netease.vopen.app.VopenApplication {
    <init>(...);
}

-keep class com.tencent.tinker.loader.** {
    <init>(...);
}

