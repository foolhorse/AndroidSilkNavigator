package me.machao.silknavigator.processor

import com.google.auto.service.AutoService
import me.machao.silknavigator.anno.Route
import java.io.File
import java.io.IOException
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic
import javax.lang.model.type.TypeKind
import javax.lang.model.type.TypeMirror
import javax.lang.model.type.TypeVariable
import javax.lang.model.type.DeclaredType
import javax.annotation.processing.Filer
import javax.lang.model.element.PackageElement


@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions(RouteProcessor.KAPT_KOTLIN_GENERATED_OPTION_NAME)
class RouteProcessor : AbstractProcessor() {
    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
        const val ACTIVITY_TYPE = "android.app.Activity"

    }

    private val generatedSourcesRoot by lazy { processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME].orEmpty() }
    private var filer: Filer? = null
    private lateinit var processingEnvironment: ProcessingEnvironment

    override fun init(env: ProcessingEnvironment?) {
        super.init(env)
        filer = env!!.filer
        processingEnvironment = env
    }

    override fun getSupportedAnnotationTypes(): Set<String> {
        return mutableSetOf(Route::class.java.canonicalName)
    }

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment?): Boolean {
        printWarning("apt process start")
        val navBuilder = NavBuilder()
        roundEnv?.getElementsAnnotatedWith(Route::class.java)?.forEach { element ->
            val hasError =
                isInaccessibleViaGeneratedCode(Route::class.java, element) ||
                        isBindingInWrongPackage(Route::class.java, element) ||
                        isExtendsFromActivity(element)

            if (hasError) {
                return false
            }

            val packageElement = processingEnv.elementUtils.getPackageOf(element)
            printWarning("apt process find a target: " + processingEnvironment.options["moduleName"] + " " + packageElement.qualifiedName + "  " + element.simpleName)

            navBuilder.addRoute(element)

            val kotlinFileSpec = navBuilder.brewKotlin("RouteSet_" + processingEnvironment.options["moduleName"])
            try {
                val file = File(generatedSourcesRoot)
                file.mkdir()
                kotlinFileSpec.writeTo(file)
            } catch (e: Exception) {
                printError(String.format("Unable to write binding for type %s: %s", element as TypeElement, e.message))
            }

//            injectFieldValue(element)
        }

        if (generatedSourcesRoot.isEmpty()) {
            printError("Can't find the target directory for generated Kotlin files.")
            return false
        }

        return false
    }

//    private fun injectFieldValue(fieldElement: Element) {
//        if (fieldElement.kind != ElementKind.CLASS) {
//            printError("Can only be applied to field, element: $classElement")
//            return false
//        }
//        val packageOfMethod = processingEnv.elementUtils.getPackageOf(fieldElement).toString()
//
//        val funcBuilder = FunSpec.builder("injectFieldValue")
//            .addModifiers(KModifier.PUBLIC)
//            .addParameter("parent", fieldElement.enclosingElement.asType().asTypeName())
//            .addStatement(
//                "parent.%L = %L",
//                fieldElement.simpleName,
//                fieldElement.getAnnotation(Bind::class.java).value
//            )
//            .addStatement(
//                "\$T.out.println(\"\$L + \$L\")",
//                System::class.java,
//                fieldElement.getAnnotation(Bind::class.java).value,
//                fieldElement.simpleName
//            )
//
//        val file = File(generatedSourcesRoot)
//        file.mkdir()
//        FileSpec.builder(packageOfMethod, "Nav").addFunction(funcBuilder.build()).build().writeTo(file)
//    }

    private fun isExtendsFromActivity(element: Element): Boolean {
        printWarning("apt process isExtendsFromActivity")

        var hasError = false

        var elementType = element.asType()

        if (elementType.kind == TypeKind.TYPEVAR) {
            val typeVariable = elementType as TypeVariable
            elementType = typeVariable.upperBound
        }
        if (!isSubtypeOfType(elementType, ACTIVITY_TYPE)) {
            printError(
                String.format(
                    "@%s must extend from Activity. (%s.%s)",
                    Route::class.java.simpleName,
                    (element.enclosedElements as TypeElement).qualifiedName,
                    element.simpleName
                )
            )
            hasError = true

        }
        return hasError
    }

    private fun isInaccessibleViaGeneratedCode(
        annotationClass: Class<out Annotation>,
        element: Element
    ): Boolean {
        printWarning("apt process isInaccessibleViaGeneratedCode")

        var hasError = false

        // Verify modifiers.
        if (element.modifiers.contains(Modifier.PRIVATE) || element.modifiers.contains(Modifier.STATIC)) {
            printError(
                String.format(
                    "@%s must not be private or static. (%s.%s)",
                    arrayOf(
                        annotationClass.simpleName,
                        (element.enclosingElement as TypeElement).qualifiedName,
                        element.simpleName
                    )
                )
            )
            hasError = true
        }

        return hasError
    }

    private fun isBindingInWrongPackage(
        annotationClass: Class<out Annotation>,
        element: Element
    ): Boolean {
        printWarning("apt process isBindingInWrongPackage")

        val enclosingElement = if (element.enclosingElement is PackageElement) {
            element as TypeElement
        } else {
            element.enclosingElement as TypeElement
        }

        val qualifiedName = enclosingElement.qualifiedName.toString()

        if (qualifiedName.startsWith("android.")) {
            printError(
                String.format(
                    "@%s-annotated class incorrectly in Android framework package. (%s)",
                    annotationClass.simpleName, qualifiedName
                )
            )
            return true
        }
        if (qualifiedName.startsWith("java.")) {
            printError(
                String.format(
                    "@%s-annotated class incorrectly in Java framework package. (%s)",
                    annotationClass.simpleName, qualifiedName
                )
            )
            return true
        }

        return false
    }

    private fun isTypeEqual(typeMirror: TypeMirror, otherType: String): Boolean {
        return otherType == typeMirror.toString()
    }

    private fun isSubtypeOfType(typeMirror: TypeMirror, otherType: String?): Boolean {
        if (isTypeEqual(typeMirror, otherType!!)) {
            return true
        }
        if (typeMirror.kind != TypeKind.DECLARED) {
            return false
        }
        val declaredType = typeMirror as DeclaredType
        val typeArguments = declaredType.typeArguments
        if (typeArguments.size > 0) {
            val typeString = StringBuilder(declaredType.asElement().toString())
            typeString.append('<')
            for (i in typeArguments.indices) {
                if (i > 0) {
                    typeString.append(',')
                }
                typeString.append('?')
            }
            typeString.append('>')
            if (typeString.toString() == otherType) {
                return true
            }
        }
        val element = declaredType.asElement() as? TypeElement ?: return false
        val superType = element.superclass
        if (isSubtypeOfType(superType, otherType)) {
            return true
        }
        for (interfaceType in element.interfaces) {
            if (isSubtypeOfType(interfaceType, otherType)) {
                return true
            }
        }
        return false
    }

    private fun printError(message: String) {
        printMessage(Diagnostic.Kind.ERROR, message)
    }

    private fun printWarning(message: String) {
        printMessage(Diagnostic.Kind.WARNING, message)
    }

    private fun printNote(message: String) {
        printMessage(Diagnostic.Kind.NOTE, message)
    }

    private fun printMessage(kind: Diagnostic.Kind, message: String) {
        processingEnv.messager.printMessage(kind, message)
    }

}
