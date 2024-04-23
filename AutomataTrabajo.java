package AutomataTrabajo;

import java.util.Scanner;
import java.util.TreeSet;

public class AutomataTrabajo {

    public static void main(String[] args) {
        Scanner digitar = new Scanner(System.in).useDelimiter("\\s*\n\\s*");
        boolean error[] = new boolean[5];
        boolean aux = false;
        error[4]=false;
        String[] linea = new String[6];
       
        for(int i=0;i<6;i++){
           linea[i] = digitar.next();
        }             
        
        String[] nomEstados = linea[0].split(" ");  
        for (String estadoName : nomEstados) {
            error[0] = estadoName.contentEquals("#")||estadoName.contentEquals("\"")||estadoName.contentEquals("'")||estadoName.contentEquals(",")||estadoName.contentEquals(".")||estadoName.contentEquals("_");
        }
            
        String[] caractAdmitidos= linea[1].split(" "); 
        for (String caracter : caractAdmitidos) {
            if(caracter.length()>1||caracter.contentEquals("#")||caracter.contentEquals("\"")||caracter.contentEquals("'")||caracter.contentEquals(",")||caracter.contentEquals(".")||caracter.contentEquals("_")){
            error[1] = true;
            break;
            }
            error[1] = false;
        }
        caractAdmitidos= (linea[1]+" #").split(" ");
        String estado_inicial = null; 
        for (String estado : nomEstados) {
            if (linea[2].contentEquals(estado)) {
                estado_inicial = linea[2];
                error[2] = false;
                break;
            }
            error[2] = true;
        }

        String[] Est_Finales = linea[3].split(" ");
        for (String EstFinal : Est_Finales) {
            boolean hay = false;
            for (String nombres_estado : nomEstados) {
                if(nombres_estado.contentEquals(EstFinal)){
                    hay = true;
                    break;
                }
            }
            if(!hay){
                error[3] = true;
                break;
            }
            error[3] = false;
        }        
        String[] listaTransiciones = linea[4].split(" ");
        try{
            for (String Transi : listaTransiciones) {
            boolean in1 = false;
            boolean fin1 = false;
            boolean char1 = false;
            
            for (int j = 0; j<nomEstados.length&&!in1; j++) {
                if (nomEstados[j].contentEquals(Transi.split(",")[0].substring(1))) {
                    in1 = true;
                }
            }           
            for (int j = 0; j<caractAdmitidos.length&&!char1; j++) {
                if (caractAdmitidos[j].contentEquals(Transi.split(",")[1])) {
                    char1 = true;
                }
            }
            
            for (int j = 0;j<nomEstados.length&&!fin1; j++) {
                if (nomEstados[j].contentEquals(Transi.split(",")[0].substring(1))) {
                    fin1 = true;
                }
            }
            if (!in1||!fin1||!char1) {
                error[4] = true;
                break;
            }
        }
        }catch(Exception e){
            error[4] = true;
        }
        
        if((error[0]&&!error[1]&&!error[2]&&!error[3]&&!error[4])||(!error[0]&&error[1]&&!error[2]&&!error[3]&&!error[4])||(!error[0]&&!error[1]&&error[2]&&!error[3]&&!error[4])||(!error[0]&&!error[1]&&!error[2]&&error[3]&&!error[4])||(!error[0]&&!error[1]&&!error[2]&&!error[3]&&error[4])){
            System.out.print("Error encontrado en ");
            for(int i = 0; i<error.length;i++){
                if(error[i]){
                    System.out.println(""+(i+1));
                    return;
                }
            }
        }else if(error[0]||error[1]||error[2]||error[3]||error[4]){
            System.out.print("Errores encontrados en");
            for(int i = 0; i<error.length;i++){
                if(error[i]){
                    System.out.print(" "+(i+1));
                    
                }
            }
            System.out.println("");
            return;
        }
        
        Estado [] Automata = new Estado[nomEstados.length];
        Estado inicial = null;
                
        for(int i = 0;i<nomEstados.length;i++){
            Automata[i] = new Estado(nomEstados[i]);                        
            if(inicial==null&&Automata[i].Nombre.contentEquals(estado_inicial)){
                inicial = Automata[i];
            }            
            for (String Estados_Finale : Est_Finales) {
                if (Automata[i].Nombre.contentEquals(Estados_Finale)) {
                    Automata[i].esFinal = true;
                }
            }
        }        
        for (String Transi : listaTransiciones) {
            Estado in1 = null;
            Estado fin1 = null;
            for (int j = 0; j<Automata.length; j++) {
                if (Automata[j].Nombre.contentEquals(Transi.split(",")[0].substring(1))) {
                    in1 = Automata[j];
                    j = Automata.length;
                }
            }
            for (int j = 0; j<Automata.length; j++) {
                if (Automata[j].Nombre.contentEquals(Transi.split(",")[2].substring(0, Transi.split(",")[2].length()-1))) {
                    fin1 = Automata[j];
                    j = Automata.length;
                }
            }
            if (in1!=null&&fin1!=null) {
                aux = aux||("#".contentEquals(Transi.split(",")[1]));
                in1.addTransicion(Transi.split(",")[1], fin1);
            }
        }       
        if(aux){
            boolean leerP_Afnde = inicial.leerP_Afnde(linea[5]);
        }else{
            boolean leerP_Afnd = inicial.leerP_Afnd(linea[5]);
        }        
    }    
}

class Estado implements Comparable{
        String Nombre;
        Boolean esFinal = false;
        Estado[] Transiciones = new Estado[0];
        String[] Indices = new String[0];
           
        Estado(String d){
            Nombre = d;
        }
        
        public int compareTo(Object x){
            if(x.getClass().equals(this.getClass())){
                return this.compareTo(Estado.class.cast(x));
            }
            return 0;
        }
        
        int compareTo(Estado x){
            return this.Nombre.compareTo(x.Nombre);
        }

        TreeSet<Estado> recorridoVacio(){
            TreeSet<Estado> explorado = new TreeSet<Estado>();
            explorado.add(this);
            return this.recorridoVacio(this,explorado);
        }
        
        public boolean equals(Object x){
            if(x.getClass().equals(this.getClass())){
                return this.equals(x.getClass().cast(x));
            }
            return false;
        }
        
        boolean equals(Estado x){
            return x.Nombre.contentEquals(this.Nombre);
        }
       
        TreeSet<Estado> recorridoVacio(Estado actual,TreeSet<Estado> Explorado){
            
            for(int i = 0;i<actual.Transiciones.length;i++){
                if(actual.Indices[i].contentEquals("#")&&!Explorado.contains(actual.Transiciones[i])){
                    Explorado.add(actual.Transiciones[i]);
                    Explorado.addAll(recorridoVacio(actual.Transiciones[i], Explorado));
                }
            }
            
            return Explorado;
        }

        boolean leerP_Afd(String palabra){
            Estado AuxEst = this, AuxNext = this;
            boolean lectura = false, rechazada = false;
            System.out.println("_"+palabra+" "+AuxEst.Nombre);
            for(int i = 0; i<palabra.length();i++){
                boolean pass = false;
                for(int j=0;j<AuxEst.Indices.length&&!pass;j++){
                    if(AuxEst.Indices[j].contentEquals(""+palabra.charAt(i))){
                        AuxNext = AuxEst.Transiciones[j];
                        pass = true;
                    }
                }
                if(!pass){
                    i = palabra.length();
                    rechazada = true;
                }
                lectura = AuxNext.esFinal;
                System.out.println(palabra.substring(0,i+1)+"_"+palabra.substring(i+1)+" "+AuxNext.Nombre);
                AuxEst = AuxNext;
            }
            
            if(AuxEst.esFinal&&!rechazada){
                System.out.println("Aprobado");
            }else{
                System.out.println("Rechazado");
            }
            
            return lectura;
        }

        boolean leerP_Afnd(String palabra){
            TreeSet<Estado> Estados0 = new TreeSet<Estado>(),Estados1 = new TreeSet<Estado>();
            Estados0.add(this);
            boolean lectura = false, Aprobada = true;
            System.out.println("_"+palabra+" "+Estados0.first().Nombre);

            for(int i = 0; i<palabra.length();i++){
                if(Estados0.size()==1){
                    Estado x = Estados0.first();
                    for(int j=0;j<x.Indices.length;j++){
                        if(x.Indices[j].contentEquals(""+palabra.charAt(i))){
                            Estados1.add(x.Transiciones[j]);
                        }
                    }                    
                }else if (Estados0.size()>1){
                    for(Estado x : Estados0){
                        for(int j=0;j<x.Indices.length;j++){
                            if(x.Indices[j].contentEquals(""+palabra.charAt(i))){
                                Estados1.add(x.Transiciones[j]);
                            }
                        }
                    }
                }
                if(Estados1.isEmpty()){
                        i = palabra.length();
                        Aprobada = false;
                }else{
                System.out.print(palabra.substring(0,i+1)+"_"+palabra.substring(i+1)+" ");
                for(Estado x:Estados1){
                        System.out.print(x.Nombre);
                    }                        
                System.out.println("");
                if(i>=palabra.length()-1){
                    for(Estado x:Estados1){
                        lectura = lectura||x.esFinal;
                    }
                }else{
                    Estados0 = Estados1;
                    Estados1 = new TreeSet<Estado>();
                }
                }
            }
            
            if(lectura&&Aprobada){
                System.out.println("Aprobado");
            }else{
                System.out.println("Rechazado");
            }
            
            return lectura;
        }
        
        boolean leerP_Afnde(String palabra){
            TreeSet<Estado> Estados0 = new TreeSet<Estado>(),Estados1 = new TreeSet<Estado>();
            Estados0.addAll(this.recorridoVacio());
            boolean lectura = false, Aceptada = true;
            System.out.println("_"+palabra+" "+this.Nombre);
            if(Estados0.size()>=1){
            System.out.print("_"+palabra+" ");
            for(Estado x:Estados0){
                System.out.print(x.Nombre);
            }                        
            System.out.println("");
            }

            for(int i = 0; i<palabra.length();i++){
                if(Estados0.size()==1){
                    Estado x = Estados0.first();
                    for(int j=0;j<x.Indices.length;j++){
                        if(x.Indices[j].contentEquals(""+palabra.charAt(i))){
                            Estados1.add(x.Transiciones[j]);
                        }
                    }   
                }else if (Estados0.size()>1){
                    for(Estado x : Estados0){
                        for(int j=0;j<x.Indices.length;j++){
                            if(x.Indices[j].contentEquals(""+palabra.charAt(i))){
                                Estados1.add(x.Transiciones[j]);
                            }
                        }
                    }
                }
                if(Estados1.isEmpty()){
                        i = palabra.length();
                        Aceptada = false;
                }else{
                System.out.print(palabra.substring(0,i+1)+"_"+palabra.substring(i+1)+" ");
                for(Estado x:Estados1){
                        System.out.print(x.Nombre);
                    }
                System.out.println("");
                
                if(i>=palabra.length()-1){
                    for(Estado x:Estados1){
                        lectura = lectura||x.esFinal;
                    }
                }else{
                    Estados0 = new TreeSet<Estado>();
                    for(Estado x: Estados1){
                        Estados0.addAll(x.recorridoVacio());
                    }
                    Estados1 = new TreeSet<Estado>();
                }
                }
            }
            
            if(lectura&&Aceptada){
                System.out.println("Aceptado");
            }else{
                System.out.println("Rechazado");
            }
            
            return lectura;
        }
        
        boolean addTransicion(String a, Estado f, Estado i){
            
            if(f==null||i==null){
                return false;
            }
            
            String[] auxPuntero = new String[1+i.Indices.length];
            Estado[] auxTransicion = new Estado[i.Transiciones.length+1];
                        
            for(int x = 0; x <i.Transiciones.length;x++){
                auxTransicion[x] = Transiciones[x];
                auxPuntero[x] = Indices[x];
            }
            auxTransicion[Transiciones.length] = f;
            auxPuntero[Indices.length] = a;
            
            i.Transiciones = auxTransicion;
            i.Indices = auxPuntero;
            
            return true;
        }
        
        boolean addTransicion(String a, Estado f){
            
            if(f==null){
                return false;
            }
            
            String[] auxPuntero = new String[1+Indices.length];
            Estado[] auxTransicion = new Estado[Transiciones.length+1];            
            
            for(int x = 0; x <Transiciones.length;x++){
                auxTransicion[x] = Transiciones[x];
                auxPuntero[x] = Indices[x];
            }
            auxTransicion[Transiciones.length] = f;
            auxPuntero[Indices.length] = a;
            
            Transiciones = auxTransicion;
            Indices = auxPuntero;
            
            return true;
        }
        
        static Estado Auxiliar;  
    }
    

