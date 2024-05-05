package Mundo;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
/**
 * interface class that represents the table constraint and table type for implementation in model
 */
public @interface TableProperties {
    public String miConstraint();   
    public String miType();
}
