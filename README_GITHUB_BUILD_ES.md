# Compilar Motoroll Home APK con GitHub Actions

Este paquete contiene el proyecto Android y un flujo de GitHub Actions para compilar un APK de depuración en la nube.

## Pasos

1. Extrae el ZIP en tu PC.
2. En GitHub, crea un repositorio nuevo (puede ser privado).
3. En el repositorio, usa **Add file > Upload files** y sube el contenido de esta carpeta (incluida la carpeta oculta `.github`). No subas la carpeta contenedora como una carpeta anidada: `settings.gradle` y `app/` deben quedar en la raíz del repositorio.
4. Confirma los archivos con **Commit changes** en la rama `main`.
5. Abre la pestaña **Actions**. Se iniciará el flujo **Build Motoroll Home APK** al subir los archivos. También podrás iniciarlo desde **Run workflow**.
6. Cuando termine correctamente, abre la ejecución y descarga el artefacto **MotorollHome-debug-apk**. Dentro estará `app-debug.apk`.

El flujo configura JDK 17, Gradle 8.13 y Android SDK 36; compila `assembleDebug` y publica el APK como artefacto.

## Instalar en el Motorola

Con depuración USB activa y el teléfono conectado:

```bat
adb install -r app-debug.apk
```

Luego selecciona Motoroll Home en Ajustes > Aplicaciones > Aplicaciones predeterminadas > Aplicación de inicio.

## Notas

- Este es un APK de depuración, pensado para instalar y probar, no una versión de lanzamiento firmada para Play Store.
- El proyecto no incluye `gradlew` ni `gradle-wrapper.jar`; por eso el flujo instala Gradle 8.13 en el runner y ejecuta `gradle assembleDebug` directamente.
- El launcher de Motorola permanece instalado y puede volver a seleccionarse en cualquier momento.
