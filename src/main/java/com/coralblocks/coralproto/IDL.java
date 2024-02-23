package com.coralblocks.coralproto;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;

import com.coralblocks.coralproto.util.IOUtils;
import com.coralblocks.coralproto.util.RegexUtils;

public class IDL {
	
	private static final List<String> imports = new ArrayList<String>(8);
	
	public static void replaceAutoGeneratedCode(String pathToJavaFile, String code) throws IOException {
		replaceAutoGeneratedCode(pathToJavaFile, code, false);
	}
	
	public static String getPathToJavaCode(String srcFolder, Class<?> klass) {
		if (!srcFolder.endsWith(File.separator)) srcFolder += File.separator;
		String path = klass.getName();
		path = path.replaceAll("\\.", File.separator);
		path = srcFolder + path + ".java";
		return path;
	}
	
	public static void replaceAutoGeneratedCode(String pathToJavaFile, String code, boolean dryRun) throws IOException {

		File file = new File(pathToJavaFile);
		if (!file.exists() || file.isDirectory()) throw new RuntimeException("Cannot find file: " + pathToJavaFile);
		String fileContents = IOUtils.readFile(pathToJavaFile);
		FileOutputStream fos = new FileOutputStream(pathToJavaFile + ".tmp");
		PrintWriter pw = new PrintWriter(fos);
		String[] lines = fileContents.split("\n");
		for(int i = 0; i < lines.length; i++) {
			String line = lines[i];
			pw.println(line);
			if (line.contains("BEGIN_AUTO_GENERATED_CODE")) {
				pw.println();
				pw.println(code);
				while(!line.contains("END_AUTO_GENERATED_CODE")) {
					i++;
					line = i >= lines.length ? null : lines[i];
				}
				pw.println(line);
			}
		}
		
		pw.close();
		fos.close();
		
		if (dryRun) return;
		
		File f1 = new File(pathToJavaFile + ".tmp");
		File f2 = new File(pathToJavaFile);
		f2.delete();
		f2 = new File(pathToJavaFile);
		f1.renameTo(f2);
	}
	
	public static void writeJavaClass(String pathToJavaFile, String code, String _package, String klassName) throws IOException {
		writeJavaClass(pathToJavaFile, code, _package, klassName, false);
	}
	
	public static void writeJavaClass(String pathToJavaFile, String code, String _package, String klassName, boolean dryRun) throws IOException {

		FileOutputStream fos = new FileOutputStream(pathToJavaFile);
		PrintWriter pw = new PrintWriter(fos);
		pw.println("package " + _package + ";");
		
		pw.println();
		pw.println("import com.coralblocks.coralproto.AbstractProto;");
		pw.println("import com.coralblocks.coralproto.field.*;");
		
		if (!imports.isEmpty()) {
			for(String imp : imports) {
				pw.println(imp);
			}
		}
		
		imports.clear();
		
		pw.println();
		
		pw.println("// THIS CODE WAS AUTO-GENERATED!!! DO NOT MODIFY OR YOUR CHANGES WILL BE LOST NEXT TIME IT IS AUTO-GENERATED!!!");
		pw.println("public class " + klassName + " extends AbstractProto {");
		pw.println();
		
		pw.println(code);
		pw.println("}");
		
		pw.close();
		fos.close();
		
		if (dryRun) return;

		String pathToJavaFileMinusTmp = removeTmpExtension(pathToJavaFile);
		
		File f1 = new File(pathToJavaFile);
		File f2 = new File(pathToJavaFileMinusTmp);
		f2.delete();
		f2 = new File(pathToJavaFileMinusTmp);
		f1.renameTo(f2);
	}
	
	private static String removeTmpExtension(String s) throws IOException {
		int index = s.lastIndexOf(".tmp");
		if (index <= 0) throw new IOException("Cannot remove tmp extension: " + s);
		return s.substring(0, index);
	}
	
	public static List<File> findAllFiles(String dir, String extension) throws IOException {

		String extensionWithoutDot = new String(extension);
		if (extensionWithoutDot.startsWith(".")) {
			extensionWithoutDot = extensionWithoutDot.substring(1);
		}
		
		if (!extension.startsWith(".")) extension = "." + extension;
		
		if (dir.endsWith(File.separator)) {
			dir += extensionWithoutDot + File.separator;
		} else {
			dir += File.separator + extensionWithoutDot + File.separator;
		}

		List<File> files = Files.list(Paths.get(dir))
                .filter(Files::isRegularFile)
                .map(Path::toFile)
                .collect(Collectors.toList());
		
		List<File> list = new ArrayList<File>(files.size());
		for(File f : files) {
			if (f.getName().endsWith(extension)) list.add(f);
		}
		
		return list;
	}
	
	public static void main(String[] args) throws IOException {
		
		if (args.length < 1) {
			System.out.println("format: java com.coralblocks.coralproto.IDL <FOLDER_NAME> <DRY_RUN> <EXTENSION>");
			return;
		}
		
		String dir = args[0];
		boolean dryRun = args.length > 1 ? Boolean.parseBoolean(args[1]) : false;
		String extension = args.length > 2 ? args[2] : "idl";
		
		if (extension.startsWith(".")) {
			extension = extension.substring(1);
		}
		
		List<File> files = IDL.findAllFiles(dir, extension);
		
		if (files.isEmpty()) {
			System.out.println("No files found in folder!");
			return;
		}
		
		List<String> list = new ArrayList<String>();
		
		for(File f : files) {
			System.out.println("Found file: " + f.getName());
			String absPath = f.getAbsolutePath();
			String data = IOUtils.readFile(absPath);
			String klassName = find(data, "CLASSNAME");
			String folder = getFolder(absPath, extension);
			String simple = getSimpleClassName(klassName);
			String full = folder + simple + ".java.tmp";
			System.out.println("Generating code: " + full);
			list.add(full);
			IDL idl = new IDL(data);
			writeJavaClass(full, idl.getCode(), getPackageName(klassName), simple, dryRun);
		}
	}
	
	private static String getFolder(String absPath, String extension) throws IOException {
		int index = absPath.lastIndexOf(File.separatorChar);
		if (index <= 0) throw new IOException("Could not find folder from path: " + absPath);
		String s = absPath.substring(0, index + 1);
		String remove = extension + File.separator;
		index = s.lastIndexOf(remove);
		return s.substring(0, index);
	}
	
	private static String getSimpleClassName(String klassName) throws IOException {
		int index = klassName.lastIndexOf('.');
		if (index <= 0) throw new IOException("Could not find simple class name: " + klassName);
		return klassName.substring(index + 1, klassName.length());
	}
	
	private static String getPackageName(String klassName) throws IOException {
		int index = klassName.lastIndexOf('.');
		if (index <= 0) throw new IOException("Could not find package name: " + klassName);
		return klassName.substring(0, index);
	}
	
	public static final String INDENT = "    ";
	
	private final String idl;
	private final StringBuilder code = new StringBuilder(2048);
	
	public IDL(String idl) {
		this(idl, INDENT);
	}
	
	public IDL(String idl, String indent) {
		this.idl = idl;
		parseTypeAndSubtype(idl, indent);
		Queue<String> lines = parseLines(idl);
		Map<String, Object> map = parseMap(lines, "");
		configure(map, true, indent, null, "");
	}
	
	public String getCode() {
		return code.toString();
	}
	
	@SuppressWarnings("unchecked")
	private void configure(Map<String, Object> map, boolean addInstance, String indent, List<String> groupFields, String saveClassName) {
		Iterator<String> iter = map.keySet().iterator();
		while(iter.hasNext()) {
			String key = iter.next();
			Object type = map.get(key);
			
			if (type instanceof String) {
				// field
				String sType = (String) type;
				createFieldByType(key, sType, addInstance, indent, groupFields);
			} else {
				
				// group
				Map<String, Object> subMap = (Map<String, Object>) type;
				
				String compoundClassName = (saveClassName.length() > 0 ? saveClassName + "." : "") + key.substring(0, 1).toUpperCase() + key.substring(1) + "RepeatingGroup";
				String className = key.substring(0, 1).toUpperCase() + key.substring(1) + "RepeatingGroup";
				code.append(indent).append("public static class ").append(className).append(" extends RepeatingGroupField {\n\n");
				
				List<String> list = new ArrayList<String>();
				
				String newIndent = indent + INDENT;
				configure(subMap, false, newIndent, list, compoundClassName);
				
				code.append(newIndent).append("public " + className + "(AbstractProto proto) {\n");
				code.append(newIndent + INDENT);
				code.append("this(proto, ");
				for(int i = 0; i < list.size(); i++) {
					if (i > 0) code.append(", ");
					String s = list.get(i);
					String[] ss = s.split("\\s*=\\s*");
					if (ss.length == 2) {
						code.append(ss[1]);
					} else {
						code.append(s);
					}
				}
				code.append(");\n");
				code.append(newIndent).append("}\n\n");
				
				code.append(newIndent).append("public " + className + "(AbstractProto proto, ProtoField ... protoFields) {\n");
				code.append(newIndent + INDENT);
				code.append("super(proto, protoFields);\n");
				code.append(newIndent).append("}\n");
				
				code.append("\n");
				code.append(newIndent).append("@Override\n");
				code.append(newIndent).append("public GroupField nextElement() {\n");
				code.append(newIndent + INDENT).append("GroupField groupField = super.nextElement();\n");
				
				for(int i = 0; i < list.size(); i++) {
					String s = list.get(i);
					String[] ss = s.split("\\s*=\\s*");
					String first = ss[0];
					String second = ss[1];
					second = RegexUtils.sub(second, "s/^new //");
					second = RegexUtils.sub(second, "s/\\(.*\\)//");
					code.append(newIndent + INDENT).append(first + " = ");
					code.append("(" + second + ") groupField.internalArray()[" + i + "];\n");
				}
				
				code.append(newIndent + INDENT).append("return groupField;\n");
				
				code.append(newIndent).append("} \n\n");
				
				code.append(newIndent).append("@Override\n");
				code.append(newIndent).append("public GroupField iterNext() {\n");
				code.append(newIndent + INDENT).append("GroupField groupField = super.iterNext();\n");
				code.append(newIndent + INDENT).append("if (groupField != null) {\n");
				
				for(int i = 0; i < list.size(); i++) {
					String s = list.get(i);
					String[] ss = s.split("\\s*=\\s*");
					String first = ss[0];
					String second = ss[1];
					second = RegexUtils.sub(second, "s/^new //");
					second = RegexUtils.sub(second, "s/\\(.*\\)//");
					code.append(newIndent + INDENT + INDENT).append(first + " = ");
					code.append("(" + second + ") groupField.internalArray()[" + i + "];\n");
				}
				
				code.append(newIndent + INDENT).append("}\n");
				
				code.append(newIndent + INDENT).append("return groupField;\n");
				
				code.append(newIndent).append("} \n\n");
				
				code.append(newIndent).append("@Override\n");
				code.append(newIndent).append("protected final RepeatingGroupField newInstance(ProtoField[] protoFields) {\n");
				
				code.append(newIndent + INDENT).append("return new " + compoundClassName + "(null, protoFields);\n");
				
				code.append(newIndent).append("} \n\n");
				
				code.append(indent).append("}\n\n");
				
				code.append(indent);
				
				if (addInstance) {
					code.append("public final " + className + " " + key);
					code.append(" = new " + className + "(this);\n");
				} else {
					code.append("public " + compoundClassName + " " + key);
					code.append(";\n");
					groupFields.add("this." + key + " = new " + compoundClassName + "(null)");
				}
				code.append("\n");
			}
		}
	}
	
	private void addField(String name, boolean addThis, boolean isOptional, List<String> groupFields, String fieldType) {
		code.append(fieldType + " " + name);
		if (addThis) {
			code.append(" = new " + fieldType + "(this");
			if (isOptional) {
				code.append(", true);\n");
			} else {
				code.append(");\n");
			}
		} else {
			code.append(";\n");
			if (isOptional) {
				groupFields.add("this." + name + " = new " + fieldType + "(true)");
			} else {
				groupFields.add("this." + name + " = new " + fieldType + "()");
			}
		}
	}
	
	private void addField(String name, boolean addThis, boolean isOptional, List<String> groupFields, String fieldType, String size) {
		code.append(fieldType + " " + name);
		if (addThis) {
			code.append(" = new " + fieldType + "(this, ").append(size);
			if (isOptional) {
				code.append(", true);\n");
			} else {
				code.append(");\n");
			}
		} else {
			code.append(";\n");
			if (isOptional) {
				groupFields.add("this." + name + " = new " + fieldType + "(" + size + ", true)");
			} else {
				groupFields.add("this." + name + " = new " + fieldType + "(" + size + ")");
			}
		}
	}
	
	private void addEnumField(String name, boolean addThis, boolean isOptional, List<String> groupFields, String fieldType, String all) {
		code.append(fieldType + " " + name);
		if (addThis) {
			code.append(" = new " + fieldType + "(this, ").append(all);
			if (isOptional) {
				code.append(", true);\n");
			} else {
				code.append(");\n");
			}
		} else {
			code.append(";\n");
			if (isOptional) {
				groupFields.add("this." + name + " = new " + fieldType + "(true, " + all + ")");
			} else {
				groupFields.add("this." + name + " = new " + fieldType + "(" + all + ")");				
			}
		}
	}
	
	private void createFieldByType(String name, String type, boolean addThis, String indent, List<String> groupFields) {
		if (addThis) {
			code.append(indent).append("public final ");
		} else {
			code.append(indent).append("public ");
		}
		boolean isOptional = type.endsWith("!");
		
		if (type.startsWith("charEnum") || type.startsWith("intEnum") || type.startsWith("shortEnum") || type.startsWith("twoChar")) {
			String all = getEnumAll(type);
			String t = getEnumType(type);
			addEnumField(name, addThis, isOptional, groupFields, t, all);
		} else if (type.startsWith("double")) {
			String size = getSize(type, true);
			if (size != null) addField(name, addThis, isOptional, groupFields, "DoubleField", size);
			else addField(name, addThis, isOptional, groupFields, "DoubleField");
		} else if (type.startsWith("float")) {
			String size = getSize(type, true);
			if (size != null) addField(name, addThis, isOptional, groupFields, "FloatField", size);
			else addField(name, addThis, isOptional, groupFields, "FloatField");
		} else if (type.startsWith("long")) {
			addField(name, addThis, isOptional, groupFields, "LongField");
		} else if (type.startsWith("int")) {
			addField(name, addThis, isOptional, groupFields, "IntField");
		} else if (type.startsWith("short")) {
			addField(name, addThis, isOptional, groupFields, "ShortField");
		}  else if (type.startsWith("boolean")) {
			addField(name, addThis, isOptional, groupFields, "BooleanField");
		} else if (type.startsWith("byte") && !type.startsWith("bytes")) {
			addField(name, addThis, isOptional, groupFields, "ByteField");
		} else if (type.startsWith("char") && !type.startsWith("chars")) {
			addField(name, addThis, isOptional, groupFields, "CharField");
		} else if (type.startsWith("bytes")) {
			String size = getSize(type, false);
			addField(name, addThis, isOptional, groupFields, "BytesField", size);
		} else if (type.startsWith("varbytes")) {
			String size = getSize(type, false);
			addField(name, addThis, isOptional, groupFields, "VarBytesField", size);
		} else if (type.startsWith("chars")) {
			String size = getSize(type, false);
			addField(name, addThis, isOptional, groupFields, "CharsField", size);
		} else if (type.startsWith("varchars")) {
			String size = getSize(type, false);
			addField(name, addThis, isOptional, groupFields, "VarCharsField", size);
		} else {
			throw new IllegalStateException("Bad type: " + type);
		}
		code.append("\n");
	}
	
	private static boolean isNumber(String value) {
		try {
			Long.parseLong(value);
			return true;
		} catch(Exception e) {
			return false;
		}
	}
	
	private String getSize(String type, boolean optional) {
		String[] matches = RegexUtils.match(type, "/\\(([^\\\\)]+)\\)/");
		if (matches == null || matches.length != 1) {
			if (optional) return null;
			throw new IllegalStateException("Cannot parse type: " + type);
		}
		if (isNumber(matches[0])) return matches[0];
		int index = matches[0].lastIndexOf('.');
		String klass = matches[0].substring(0, index);
		String s = "import " + klass + ";";
		if (!imports.contains(s)) imports.add(s);
		String var = matches[0].substring(index + 1);
		index = klass.lastIndexOf('.');
		String klassName = klass.substring(index + 1);
		return klassName + "." + var; 
	}
	
	private String getEnumAll(String type) {
		String[] matches = RegexUtils.match(type, "/Enum\\(([^\\)]+)\\)/");
		if (matches == null || matches.length != 1) throw new IllegalStateException("Cannot parse all: " + type);
		String s = "import " + matches[0] + ";";
		if (!imports.contains(s)) imports.add(s);
		String[] t = matches[0].split("\\.");
		if (t.length > 1) {
			return t[t.length - 1] + ".ALL";
		} else {
			return matches[0] + ".ALL";
		}
	}
	
	private String getEnumType(String type) {
		String[] matches = RegexUtils.match(type, "/Enum\\(([^\\)]+)\\)/");
		if (matches == null || matches.length != 1) throw new IllegalStateException("Cannot parse all: " + type);
		String s = "import " + matches[0] + ";";
		if (!imports.contains(s)) imports.add(s);
		String[] t = matches[0].split("\\.");
		String prefix = null;
		if (type.startsWith("char")) {
			prefix = "Char";
		} else if (type.startsWith("int")) {
			prefix = "Int";
		} else if (type.startsWith("short")) {
			prefix = "Short";
		} else if (type.startsWith("twoChar")) {
			prefix = "TwoChar";
		}
		if (t.length > 1) {
			return prefix + "EnumField<" + t[t.length - 1] + ">";
		} else {
			return prefix + "EnumField<" + matches[0] + ">";
		}
	}
	
	private Queue<String> parseLines(String idl) {
		String[] temp = idl.split("\n");
		Queue<String> lines = new LinkedList<String>();
		String toRemove = null;
		for(String line : temp) {
			if (toRemove == null) {
				String[] matches = RegexUtils.match(line, "/^(\\s*)TYPE\\s*=/");
				if (matches != null && matches.length == 1 && matches[0].length() > 0) {
					toRemove = matches[0];
					int l = toRemove.length();
					line = line.substring(l);
				}
			} else if (line.startsWith(toRemove)) {
				int l = toRemove.length();
				line = line.substring(l);
			}
			if (line.trim().equals("") || line.startsWith("#") || !line.contains(":")) continue;
			lines.add(line);
		}
		return lines;
	}
	
	private LinkedHashMap<String, Object> parseMap(Queue<String> q, String ind) {
		
		LinkedHashMap<String, Object> map = new LinkedHashMap<>();

		while(!q.isEmpty()) {
			
			String line = q.peek();

			if (!line.startsWith(ind)) break;

			q.poll();

			int idx = line.indexOf(":");
			String name = line.substring(ind.length(), idx);
			String value = line.substring(idx + 1).trim();

			if (!value.isEmpty()) {
				map.put(name, value);
			} else {
				
				// find the indent...
				String nextLine = q.peek();
				String newIndent = findIndent(nextLine);
				
				map.put(name, parseMap(q, newIndent));
			}
		}

		return map;
	}
	
	private String findIndent(String line) {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < line.length(); i++) {
			char c = line.charAt(i);
			if (c == ' ' || c == '\t') {
				sb.append(c);
			} else {
				break;
			}
		}
		return sb.toString();
	}
	
	private void parseTypeAndSubtype(String idl, String indent) {
		String type = find("TYPE");
		if (type.length() != 1) throw new RuntimeException("Type is not a character: " + type);
		code.append(indent + "public final static char TYPE = '").append(type).append("';\n");
		
		String subtype = find("SUBTYPE");
		if (subtype.length() != 1) throw new RuntimeException("Subtype is not a character: " + subtype);
		code.append(indent + "public final static char SUBTYPE = '").append(subtype).append("';\n");
		
		code.append("\n");
		
		code.append(indent + "public final TypeField typeField = new TypeField(this, TYPE);\n");
		code.append(indent + "public final SubtypeField subtypeField = new SubtypeField(this, SUBTYPE);\n");
		
		code.append("\n");
	}
	
	private String find(String field) {
		return find(idl, field);
	}
	
	private static String find(String idl, String field) {
		String[] temp = idl.split("\n");
		for(String line: temp) {
			line = line.trim();
			String[] matches = RegexUtils.match(line, "/^" + field + "\\s*=\\s*([^\\n]+)$/");
			if (matches != null && matches.length == 1) return matches[0];
		}
		throw new RuntimeException("Required IDL field not found: " + field);
	}
}