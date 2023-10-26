package Mundo;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
/**
 * interface para la creación del constraint de las columnas del modelo
 * <br> pre: </br> el constraint se aplica al modelo, se debe crear una interface `Constraint` por modelo
 */
public @interface TableProperties {
    String miConstraint();   
    String miType();
}
