/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.truelicense.obfuscate.processor;

import java.util.Map;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import static javax.lang.model.element.Modifier.*;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import static javax.tools.Diagnostic.Kind.*;
import net.java.truelicense.obfuscate.Obfuscate;
import net.java.truelicense.obfuscate.ObfuscatedString;

/**
 * A processor for the <code>@{@link Obfuscate}</code> annotation.
 * Use this processor to enable compile time checking of annotated fields or if
 * you want (or need to) manually obfuscate their constant string values.
 * <p>
 * For every field which is annotated with {@code @Obfuscate}, this processor
 * asserts that it is not a member of an interface and has a constant string
 * value.
 * Otherwise, an error message is emitted.
 * <p>
 * If any of the processing options
 * {@code net.java.truelicense.obfuscate.verbose} or
 * {@code net.java.truelicense.obfuscate.processor.verbose}
 * is set to {@code true} (whereby case is ignored), then
 * for every field which is annotated with {@code @Obfuscate}, this processor
 * emits a note with an obfuscated Java source code expression which computes
 * the constant string value again.
 * You could copy-paste this note into the source code for manual substitution
 * if you don't want to (or cannot) use an automated build tool for this task.
 *
 * @author Christian Schlichtherle
 */
@SupportedSourceVersion(SourceVersion.RELEASE_6)
@SupportedAnnotationTypes("net.java.truelicense.obfuscate.Obfuscate")
@SupportedOptions({
    "net.java.truelicense.obfuscate.verbose",
    "net.java.truelicense.obfuscate.processor.verbose"
})
public class ObfuscateProcessor extends AbstractProcessor {

    private boolean verbose;

    @Override
    public void init(final ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        final Set<String> supported = getSupportedOptions();
        final Map<String, String> present = processingEnv.getOptions();
        verbose = false;
        for (final String option : supported)
            verbose |= Boolean.parseBoolean(present.get(option));
    }

    @Override
    public boolean process(
            final Set<? extends TypeElement> annotations,
            final RoundEnvironment roundEnv) {
        if (roundEnv.errorRaised() || roundEnv.processingOver()) return true;
        for (final TypeElement ate : annotations) {
            assert ate.asType().toString().equals(Obfuscate.class.getName());
            for (final Element e : roundEnv.getElementsAnnotatedWith(ate)) {
                final VariableElement ve = (VariableElement) e;
                final TypeElement cte = (TypeElement) ve.getEnclosingElement();
                try {
                    final String csv = (String) ve.getConstantValue();
                    if (keepField(ve))
                        warn("Obfuscation of protected or public or non-static field is insecure because it can't get removed from the byte code.", ve);
                    debug(ObfuscatedString.obfuscate(csv), ve);
                } catch (final RuntimeException ex) {
                    error("Annotated field does not have a constant string value.", ve);
                }
            }
        }
        return true;
    }

    private static boolean keepField(Element element) {
        final Set<Modifier> m = element.getModifiers();
        return  m.contains(PROTECTED) || m.contains(PUBLIC) ||
                !m.contains(STATIC);
    }

    private void debug(CharSequence msg, Element e) {
        if (verbose) getMessager().printMessage(NOTE, msg, e);
    }

    private void warn(CharSequence msg, Element e) {
        getMessager().printMessage(WARNING, msg , e);
    }

    private void error(CharSequence msg, Element e) {
        getMessager().printMessage(ERROR, msg , e);
    }

    private Messager getMessager() { return processingEnv.getMessager(); }
}
