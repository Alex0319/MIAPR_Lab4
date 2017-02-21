package by.miapr.algorithm;

import sun.plugin.javascript.navig.Array;

import javax.xml.bind.ValidationEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

/**
 * Created by user1 on 15.02.2017.
 */

public class ClassificationDeterminant {
    private static ClassificationDeterminant instance;
    private ArrayList<TestClass> arrayListTestClasses;
    private ArrayList<TestVector> arrayListWeights;
    private ArrayList<Integer> decisionFunction=new ArrayList<>();

    private ClassificationDeterminant(){}

    public static ClassificationDeterminant getInstance(){
        if(instance==null){
            instance=new ClassificationDeterminant();
        }
        return instance;
    }

    private void formStartWeights(int classCount) {
        arrayListWeights=new ArrayList<>(classCount);
        for (int i=0;i<classCount;i++){
            arrayListWeights.add(new TestVector(0,0,0,i));
        }
    }

    private void initialize(int classCount, int objectsCount,int minSignValue,int maxSignValue){
        arrayListTestClasses =new ArrayList<>(classCount);
        arrayListWeights=new ArrayList<>(classCount);
        for (int i=0;i<classCount;i++){
            arrayListTestClasses.add(new TestClass(objectsCount,minSignValue,maxSignValue,i));
        }
    }

    private ArrayList<Integer> findDecisionFunctions(TestVector vector,ArrayList<TestVector> arrayListWeights){
        ArrayList<Integer> decisionFuncValues=new ArrayList<>();
        for (TestVector weight:arrayListWeights) {
            decisionFuncValues.add(weight.getX()*vector.getX()+weight.getY()*vector.getY()+weight.getZ()*vector.getZ());
        }
        return decisionFuncValues;
    }

    private boolean check(TestVector vector,ArrayList<Integer> decisions){
        int count=0;
        for (int elem: decisions) {
            if(count!= vector.getClassNumber()){
                if(elem >= decisions.get(vector.getClassNumber())){
                    return false;
                }
            }
            count++;
        }
        return true;
    }

    private String getValue(int value,String str){
        if(value!=0 && value!=1){
            if(value>0) {
                return String.format("+ %d%s", value, str);
            }
            return String.format("%d%s",value,str);
        }
        return "";
    }

    private ArrayList<String> printResolveFunctions() {
        ArrayList<String> result=new ArrayList<>();
        int count=0;
        for (TestVector vector:arrayListWeights) {
            result.add(String.format("d%d(x) = %s %s %s",++count,vector.getX()!=0 ? vector.getX()+"*x1" : "",
                    getValue(vector.getY(),"*x2"),getValue(vector.getZ(),"")));
        }
        return result;
    }

    private ArrayList<TestVector> findWeight(TestVector vector, ArrayList<TestVector> arrayListWeights) {
        ArrayList<TestVector> newArrayListWeights=new ArrayList<>();
        for(TestVector currentVector: arrayListWeights){
            if(vector.getClassNumber()==currentVector.getClassNumber()){
                newArrayListWeights.add(new TestVector(currentVector.getX()+vector.getX(),
                        currentVector.getY()+vector.getY(),currentVector.getZ()+vector.getZ(),currentVector.getClassNumber()));
            }else{
                if(decisionFunction.get(vector.getClassNumber())<=decisionFunction.get(currentVector.getClassNumber())){
                    newArrayListWeights.add(new TestVector(currentVector.getX()-vector.getX(),
                            currentVector.getY()-vector.getY(),currentVector.getZ()-vector.getZ(),currentVector.getClassNumber()));
                }else{
                    newArrayListWeights.add(currentVector);
                }
            }
        }
        return newArrayListWeights;
    }

    private void searchResolveFunctions(int vectorsCount){
        int iteration=0;
        boolean isEnd=false,isRepeat=false;
        while(!(isEnd && isRepeat) && iteration<2000){
            for(int i=0;i<vectorsCount;i++){
                if(!isEnd || !isRepeat){
                    for(int j=0;j<arrayListTestClasses.size();j++){
                        TestVector currentVector=arrayListTestClasses.get(j).getVector(i);
                        decisionFunction=findDecisionFunctions(currentVector,arrayListWeights);
                        if(isEnd){
                            isRepeat=check(currentVector,decisionFunction);
                            if(!isRepeat){
                                isEnd=false;
                            }else{ break;}
                        }else{
                            isEnd=check(currentVector,decisionFunction);
                        }
                        if(!isEnd){
                            arrayListWeights=findWeight(currentVector,arrayListWeights);
                        }
                        decisionFunction.clear();
                        iteration++;
                    }
                }else{ break; }
            }
        }
    }

    public ArrayList<String> getLearningSample(int classCount, int objectsCount,int minSignValue,int maxSignValue){
        initialize(classCount,objectsCount,minSignValue,maxSignValue);
        ArrayList<String> resultList=new ArrayList<>();
        for (int i=0;i<arrayListTestClasses.size();i++){
            resultList.add(String.format("Класс %d",i+1));
            TestClass testClass=arrayListTestClasses.get(i);
            int vectorsCount=testClass.getVectorsCount();
            for(int j=0;j<vectorsCount;j++){
                TestVector testVector=testClass.getVector(j);
                resultList.add(String.format("Объект %d  признаки: %d; %d; %d",j+1,testVector.getX(),testVector.getY(),testVector.getZ()));
            }
        }
        return resultList;
    }

    public ArrayList<String> getResolveFunctions(){
        formStartWeights(arrayListTestClasses.size());
        searchResolveFunctions(arrayListTestClasses.get(0).getVectorsCount());
        return printResolveFunctions();
    }

    public ArrayList<String> determineObjectClass(int firstAttribute,int secondAttribute){
        ArrayList<String> result=new ArrayList<>();
        ArrayList<Integer> decisionValues=findDecisionFunctions(new TestVector(firstAttribute,secondAttribute,1,0),arrayListWeights);
        int maxValue=decisionValues.get(0),maxValueIndex=0;
        for(int i=0;i<decisionValues.size();i++){
            int currentValue=decisionValues.get(i);
            result.add("d"+(i+1)+"(x) = "+currentValue);
            if(maxValue<currentValue){
                maxValue=currentValue;
                maxValueIndex=i;
            }
        }
        result.set(maxValueIndex,result.get(maxValueIndex).concat("(max)"));
        result.addAll(Arrays.asList("","Объект относится","к классу "+(maxValueIndex+1)));
        return result;
    }
}

class TestClass{
    private ArrayList<TestVector> arrayVectors;
    private static Random rand=new Random(new Date().getTime());

    private int getRandValue(int min,int max){
        return min+rand.nextInt(max-min);
    }

    TestClass(int vectorsCount,int minSignValue,int maxSignValue,int classNumber){
        arrayVectors=new ArrayList<>(vectorsCount);
        for (int i=0;i<vectorsCount;i++){
            arrayVectors.add(new TestVector(getRandValue(minSignValue,maxSignValue),getRandValue(minSignValue,maxSignValue),1,classNumber));
        }
    }

    public TestVector getVector(int i) {
        if(i<arrayVectors.size())
            return arrayVectors.get(i);
        return null;
    }

    public int getVectorsCount(){
        return arrayVectors.size();
    }
}

class TestVector{
    private int x;
    private int y;
    private int z;
    private int classNumber;

    TestVector(int x,int y,int z,int classNumber){
        this.x=x;
        this.y=y;
        this.z=z;
        this.classNumber=classNumber;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public int getClassNumber() {
        return classNumber;
    }
}
