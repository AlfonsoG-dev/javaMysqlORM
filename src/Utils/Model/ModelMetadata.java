package Utils.Model;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;


/**
 * class that has the model annotation values which represents, constraint and type of table
 */
public class ModelMetadata {
    /**
     * model or table name
     */
    private String modelName;
    /**
     * {@link java.lang.reflect.Constructor}
     * this one is created using model name as {@link String}
     */
    public ModelMetadata(String nModelName) {
        modelName = nModelName;
    }
    /**
     * {@link java.lang.reflect.Constructor}
     * this one is created using class reflect
     */
    public ModelMetadata(Class<?> model) {
        modelName = model.getName();
    }
    /**
     * list declare class fields.
     * @throws ClassNotFoundException: error al buscar la clase
     * @return list of class fields.
     */
    private Field[] getModelFiedls() throws ClassNotFoundException {
        Class<?> miClase = Class.forName(modelName);
        Field[] myFields = miClase.getDeclaredFields();
        return myFields;
    }
    /**
     * model columns are the attributes names
     * @return columns or attributes names
     */
    private String getModelColumns() {
        StringBuffer resultado = new StringBuffer();
        try {
            Field[] myFields = getModelFiedls();
            if(myFields.length > 0) {
                for(Field f: myFields) {
                    resultado.append(f.getName() + ", ");
                }
            }
        } catch(ClassNotFoundException e) {
            System.err.println(e);
        }
        return resultado.substring(0, resultado.length()-2);
    }
    /**
     * list of class annotations.
     */
    private ArrayList<Annotation[]> getModelAnnotations() {
        ArrayList<Annotation[]> resultado = new ArrayList<>();
        try {
            Field[] myFields = getModelFiedls();
            if(myFields.length > 0) {
                for(Field f: myFields) {
                    if(f.getAnnotations().length > 0) {
                        resultado.add(f.getAnnotations());
                    }
                }
            }
        } catch(ClassNotFoundException e) {
            System.err.println(e);
        }
        return resultado;
    }
    private String getAnnotationConstraint() {
        StringBuffer constraint = new StringBuffer();
        ArrayList<Annotation[]> misAnnotations = getModelAnnotations();
        for(Annotation[] m: misAnnotations) {
            constraint.append(m[0] + " and ");
        }
        return constraint.substring(0, constraint.length()-5);
    }
    /**
     * constraint part of model annotations
     */
    private String getModelColumConstraint() {
        try {
            String[] myConstraint = getAnnotationConstraint().split(" and ");
            StringBuffer datos = new StringBuffer();
            for(String c: myConstraint) {
                String[] f = c.split(".miConstraint");
                for(String i: f) {
                    String[] ci = i.split(", ");
                    if(ci[0].startsWith("\"") || ci[0].endsWith("\"")) {
                        datos.append(ci[0]);
                    }
                }
            }
            String 
                restDatos  = datos.toString().replace("=", ", "),
                cleanDatos = restDatos.substring(2, restDatos.length());
            return cleanDatos;
        } catch(Exception e) {
            System.err.println(e);
            return "";
        }
    }
    /**
     * type part of model annotations
     */
    private String getModelColumnType() {
        try {
            String[] myConstraint = getAnnotationConstraint().split(" and ");
            StringBuffer datos = new StringBuffer();
            for(String c: myConstraint) {
                String[] f = c.split(".miConstraint");
                for(String i: f) {
                    String[] ci = i.split(", ");
                    for(String t: ci) {
                        String[] mt = t.split("miType=");
                        if(mt.length == 2) {
                            if(mt[1].startsWith("\"") || mt[1].endsWith(")")) {
                                datos.append(mt[1]);
                            }
                        }
                    }
                }
            }
            String 
                restDatos  = datos.toString().replace("\")", "\", "),
                cleanDatos = restDatos.substring(0, restDatos.length()-2);
            return cleanDatos;
        } catch(Exception e) {
            System.err.println(e);
            return "";
        }
    }
    /**
     * model column: type format.
     */
    public String getModelProperties() {
        StringBuffer build = new StringBuffer();
        String[] 
            columns    = getModelColumns().split(", "),
            types      = getModelColumnType().split(", "),
            constraint = getModelColumConstraint().split(", ");
        for(int i=0; i<columns.length; ++i) {
            if(constraint[i] != "") {
                build.append(
                        columns[i] + ": " + types[i] + " " + constraint[i] + "\n"
                );
            }
        }
        return build.toString().replace("\"", "");
    }
}
