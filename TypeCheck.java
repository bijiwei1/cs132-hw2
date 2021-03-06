import syntaxtree.*;
import visitor.*;
import java.util.*;


public class Typecheck {
	public static void main(String[] args) {
		try {
			Node root = new MiniJavaParser(System.in).Goal();
			System.out.println("Parse successful");
			
			//Load all classes
			List<ClassType> classList = new ArrayList<ClassType>();
			ClassVisitor cv = new ClassVisitor();
			root.accept(cv, classList);		
			
			//Load all methods to classes
			//MethodVisitor mv = new MethodVisitor(classList);
			//root.accept(mv, null);
			
		}catch (ParseException e) {
			System.err.println(e);
			System.exit(1);
		}
	}
}

	
//generic type for mini java
class GType {
	
	public static GType getType(Type n, ClassType curr_class) {
		GType type;
		if (n.f0.choice instanceof IntegerType) {
			type = new IntType();
		} else if (n.f0.choice instanceof BooleanType) {
			type = new BoolType();
		} else if (n.f0.choice instanceof ArrayType) {
			type = new ArrayType();
		} else if (n.f0.choice instanceof Identifier) {
			type = null; //??
		} else {
			type = null;
		}
		return type;
	}
}

//Type: Class 
class ClassType extends GType{
	public String class_name;
	public ClassType super_class;
	public GType args;
	public List<Method> methods = new ArrayList<Method>();
	public Type fields;

	
	//constructor
	ClassType (String class_name, ClassType super_class,GType args, List<Method> methods, Type fields ){
		this.class_name = class_name;
		this.super_class = super_class;
		this.args = args;
		this.methods = methods;
		this.fields = fields;
	}

	public static ClassType getClassType(String className, List<ClassType> classList) {
		for (ClassType ct : classList)
			if (className.equals(ct.class_name)){
				return ct;
			}
		return null;
	}

}

//Type: Method
class Method extends GType{
	String method_name;
	GType return_value;
	List<GType> args;
	
	//constructor 
	Method(String method_name, GType return_value, List<GType> args){
		this.method_name = method_name;
		this.return_value= return_value;
		this.args = args;
	}
	
}

class ClassVisitor extends GJVoidDepthFirst<List<ClassType>>{
	
	public void visit(MainClass n, List<ClassType> classList) {
		String cname = n.f1.f0.toString();
		//Arg for mainclass should be String[]
		ClassType newclass = new ClassType(cname, null, null, null, null);
		System.out.println("Add new class " + cname);
		classList.add(newclass);
		
		//
	}
	
	public void visit(ClassDeclaration n, List<ClassType> classList) {
		//load class into class tree
		String cname = n.f1.f0.toString();	
		ClassType newclass = new ClassType(cname, null, null, null, null);
		System.out.println("Add new class " + cname);
		classList.add(newclass);
		
		//load methods and args into class
		MethodVisitor mv = new MethodVisitor(newclass);
		n.f4.accept(mv, null);
	}
	

	public void visit(ClassExtendsDeclaration n, List<ClassType> classList) {
		String cname = n.f1.f0.toString();
		ClassType newclass = new ClassType(cname, null, null, null, null);
		System.out.println("Add new class " + cname);
		classList.add(newclass);
		
		//load methods and args into class
		MethodVisitor mv = new MethodVisitor(newclass);
		n.f6.accept(mv, null);
	}

}


class MethodVisitor extends GJVoidDepthFirst<List<GType>>{
	
	ClassType curr_class;
	
	//constructor 
	MethodVisitor(ClassType curr_class){
		this.curr_class = curr_class;
	}
	
	//TypeDeclaration  
	public void visit(Goal n, List<GType> methodArgs) {
		n.f1.accept(this, methodArgs);
	}

	// MethodDeclaration()
	//public void visit(ClassDeclaration n, List<GType> methodArgs) {
	//	n.f4.accept(this, methodArgs);
	//}
	
	// MethodDeclaration
	//public void visit(ClassExtendsDeclaration n, List<GType> methodArgs) {
	//	n.f6.accept(this, methodArgs);
	//}
	
	public void visit(MethodDeclaration n, List<GType> methodArgs) {
		String method_name = n.f2.f0.toString();
		GType return_value = GType.getType(n.f1,curr_class);
		List<GType> args = new ArrayList<GType>();
		n.f4.accept(this, args);
		Method m = new Method(method_name, return_value, args); 
		//ClassType curr_class = classlist.getClass();
		curr_class.methods.add(m);		
		System.out.println("Add new method " + method_name);
	}
}


class IntType extends GType{
	
}

class BoolType extends GType{
	
}

class ArrayType extends GType{
	
}

class Identifier extends GType{
	
}






