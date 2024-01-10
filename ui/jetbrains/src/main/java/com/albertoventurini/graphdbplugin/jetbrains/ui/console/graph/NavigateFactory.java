package com.albertoventurini.graphdbplugin.jetbrains.ui.console.graph;

import com.albertoventurini.graphdbplugin.database.api.data.GraphNode;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.usageView.UsageInfo;

import java.util.Collection;

/**
 * @author wh1t3p1g
 * @project tabby-intellij-plugin
 * @since 2024/1/9
 */
public class NavigateFactory {

    public static PsiMethod getMethod(Project project, GraphNode node) {
        try{
            String classname = (String) node.getPropertyContainer().getProperties().get("CLASSNAME");
            String methodName = (String) node.getPropertyContainer().getProperties().get("NAME");
            String signature = (String) node.getPropertyContainer().getProperties().get("SIGNATURE");
            JavaPsiFacade facade = JavaPsiFacade.getInstance(project);
            PsiClass psiClass = facade.findClass(classname, GlobalSearchScope.allScope(project));
            if (psiClass != null) {
                String[] types = getParameterTypes(signature);
                String shortClassname = classname.substring(classname.lastIndexOf(".")+1);
                PsiMethod[] methods = psiClass.getAllMethods();
                for(PsiMethod method:methods){
                    String realMethodName = methodName;
                    if(methodName.equals("<init>")){
                        realMethodName = shortClassname;
                    }

                    if(realMethodName.equals(method.getName()) && isParameterTypeEquals(method.getParameterList(), types)){
                        return method;
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static PsiCallExpression getFirstCallExpression(PsiMethod startMethod, GraphNode endNode){
        String calleeName = (String) endNode.getPropertyContainer().getProperties().get("NAME");
        if(startMethod != null && calleeName != null){
            calleeName = calleeName+"(";
            UsageInfo usage = new UsageInfo(startMethod);
            PsiMethod psiMethod = PsiTreeUtil.getParentOfType(usage.getFile().findElementAt(usage.getSegment().getEndOffset()), PsiMethod.class);
            Collection<PsiCallExpression> callExpressions = PsiTreeUtil.collectElementsOfType(psiMethod, PsiCallExpression.class);
            for(PsiCallExpression callExpression:callExpressions){
                if(callExpression.toString().contains(calleeName)){
                    return callExpression;
                }
            }
        }
        return null;
    }

    public static boolean isParameterTypeEquals(PsiParameterList psiParameterList, String[] types){
        int length = types.length;
        int curLength = psiParameterList.getParametersCount();
        if(length != curLength) return false;
        if(length == 0) return true;
        for(int index=0; index < length; index++){
            String type = types[index];
            PsiParameter parameter = psiParameterList.getParameter(index);
            if(!isSameType(parameter.getType(), type)){
                return false;
            }
        }
        return true;
    }

    public static boolean isParameterTypeEquals(PsiType[] psiTypes, String[] types){
        int length = types.length;
        int curLength = psiTypes.length;
        if(length != curLength) return false;
        if(length == 0) return true;
        for(int index=0; index < length; index++){
            String type = types[index];
            PsiType psiType = psiTypes[index];
            if(!isSameType(psiType, type)){
                return false;
            }
        }
        return true;
    }

    public static boolean isSameType(PsiType psiType, String type){
        if("java.lang.Object".equals(type)) return true;

        String psiTypeText = psiType.getCanonicalText();
        if(psiTypeText.endsWith("...") && type.endsWith("[]")){
            String realType = psiTypeText.replace("...", "");
            String curRealType = type.replace("[]", "");
            if("java.lang.Object".equals(curRealType)) return true;

            if(!realType.equals(curRealType)) return false;
        }else if(!psiType.equalsToText(type)){
            return false;
        }

        return true;
    }

    public static String[] getParameterTypes(String signature){
        int leftPos = signature.indexOf("(");
        int rightPos = signature.indexOf(")");
        String types = signature.substring(leftPos + 1, rightPos);
        if(types.isEmpty()){
            return new String[0];
        }else{
            return types.split(",");
        }
    }
}
