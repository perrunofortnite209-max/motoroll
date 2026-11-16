# Motoroll Home 1.0 — proyecto inicial

Launcher independiente para Motoroll OS, diseñado para la base identificada:

- Motorola Edge 70 Fusion (`marvel`)
- Android 16 / API 36
- Hello UI stock
- Resolución informada: 1272 × 2772
- Densidad informada: 450 dpi

## Qué incluye

- Actividad HOME independiente (`com.motoroll.home`).
- Interfaz oscura negro/azul/violeta.
- Reloj y fecha del sistema.
- Buscador y cajón de aplicaciones.
- Accesos a Ajustes, Cámara, GameTime, Teléfono y Mensajes.
- Iconos reales de las apps instaladas.
- No reemplaza ni desinstala `com.motorola.launcher3`.
- No modifica SystemUI, cámara, biometría, overlays de Motorola ni particiones.
- No solicita permisos privilegiados.

## Compilar

Este paquete es el **código fuente**, no un APK ya compilado. Para generar el APK:

1. Instala Android Studio y un JDK 17.
2. En Android Studio, abre la carpeta `MotorollHome_Project`.
3. Instala Android SDK Platform 36 cuando Android Studio lo solicite.
4. Ejecuta `Build > Build APK(s)`.
5. El APK de depuración aparecerá normalmente en `app/build/outputs/apk/debug/app-debug.apk`.

El proyecto utiliza Android Gradle Plugin 8.13.0 y Gradle 8.13.

## Instalar en el teléfono

Con el teléfono conectado y depuración USB activa:

```bat
adb install -r app-debug.apk
```

Después pulsa Inicio. Android debería permitir elegir Motoroll Home como aplicación de inicio. Para cambiarlo manualmente:

`Ajustes > Aplicaciones > Aplicaciones predeterminadas > Aplicación de inicio`

Selecciona **Motoroll Home**.

## Volver a Hello UI

En el mismo menú selecciona el launcher Motorola original. El paquete `com.motorola.launcher3` permanece instalado. También puedes desinstalar Motoroll Home:

```bat
adb uninstall com.motoroll.home
```

## Limitaciones de esta primera versión

Esta es una primera base funcional de launcher, no una sustitución de SystemUI ni un tema integral de ROM. El aspecto del panel de notificaciones, pantalla de bloqueo y Ajustes del sistema seguirá siendo el de Hello UI hasta desarrollar componentes compatibles por separado.
