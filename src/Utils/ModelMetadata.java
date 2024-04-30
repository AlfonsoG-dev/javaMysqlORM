package Utils;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;


/**
 * clase para obtener las propiedades del modelo
 */
public class ModelMetadata {
    /**
     * nombre del modelo
     */
    private String modelName;
    /**
     * constructor
     */
    public ModelMetadata(String nModelName) {
        modelName = nModelName;
    }
    public ModelMetadata(Class<?> model) {
        modelName = model.getName();
    }
    /**
     * lista de campos de la clase
     * <br> pre: </br> los campos de la clase deben ser `public`
     * @param model: nombre de la clase
     * @throws ClassNotFoundException: error al buscar la clase
     * @return la lista de los campos de la clase
     */
    private Field[] getModelFiedls() throws ClassNotFoundException {
        Class<?> miClase = Class.forName(modelName);
        Field[] myFields = miClase.getDeclaredFields();
        return myFields;
    }
    /**
     * generar las columnas del modelo en base a los campos del mismo
     * @param model: nombre del modelo
     * @return las columnas del modelo
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
     * genera una lista de annotations del modelo
     * @return la lista de annotations del modelo
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
    /**
     * genera un String con los datos de las annotations segun un tipo
     * @param type: el tipo de annotations a generar
     * @return un String con los datos de las annotations
     */
    private String getAnnotationConstraint() {
        StringBuffer constraint = new StringBuffer();
        ArrayList<Annotation[]> misAnnotations = getModelAnnotations();
        for(Annotation[] m: misAnnotations) {
            constraint.append(m[0] + " and ");
        }
        return constraint.substring(0, constraint.length()-5);
    }
    /**
     * genera un String con el constraint de cada columna en orden
     * <br> pre: </br> el orden en el que se crean los datos debe ser el mismo de las annotations
     * @return un String con los constraint de cada columna
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
     * genera un String con el tipo de dato de cada columna
     * <br> pre: </br> el orden en el que se crean los datos debe ser el mismo de las annotations
     * @return un String con el tipo de dato de cada columna
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
     * genera un String con los datos combinados del modelo
     * @return el string con los datos del modelo
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
