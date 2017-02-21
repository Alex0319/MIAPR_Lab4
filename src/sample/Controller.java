package sample;

import by.miapr.algorithm.ClassificationDeterminant;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.function.UnaryOperator;

public class Controller {

    private final int minAttributeValue=-10;
    private final int maxAttributeValue=10;
    private final ClassificationDeterminant classificationDeterminant=ClassificationDeterminant.getInstance();
    private String currentTextFieldValue;
    public TextField txtfldClassCount;
    public TextField txtfldClassObjectsCount;
    public TextField txtfldFirstAttribute;
    public TextField txtfldSecondAttribute;
    public ListView lstvwLearningSample;
    public ListView lstvwFuncValues;
    public ListView lstvwResolveFunctions;
    public Button btnDetermineClass;
    public Button btnClassify;

    private void showAlert(Alert.AlertType type, String title, String headerText, String textError){
        Alert alert=new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(textError);
        alert.show();
    }

    private void setDisable(boolean isDisable){

    }

    public void initialize(){
        UnaryOperator<TextFormatter.Change> filter=change -> {
            String text=change.getText();
            if(change.getControlText().concat(text).length()<=5 || change.isDeleted() || change.isReplaced()){
                if(text.matches("[0-9]*") || (text.equals("-") && (change.getControlText().isEmpty()))){
                    return change;
                }
            }
            return null;
        };
        txtfldClassCount.setTextFormatter(new TextFormatter<String>(filter));
        txtfldClassObjectsCount.setTextFormatter(new TextFormatter<String>(filter));
        txtfldFirstAttribute.setTextFormatter(new TextFormatter<String>(filter));
        txtfldSecondAttribute.setTextFormatter(new TextFormatter<String>(filter));
    }

    private boolean checkInputValues(int value,int minValue,int maxValue,String valueName){
        if(value>maxValue || value<minValue){
            showAlert(Alert.AlertType.ERROR,"Ошибка","Некорректный ввод",
                    String.format("%s должно быть от %d до %d",valueName,minValue,maxValue));
            return false;
        }
        return true;
    }

    private void showListViewResults(ListView listView,ArrayList<String> learningSample) {
        listView.setItems(FXCollections.observableArrayList(learningSample));
    }

    private void setAttributesState(boolean state){
        txtfldFirstAttribute.setDisable(state);
        txtfldSecondAttribute.setDisable(state);
        btnDetermineClass.setDisable(state);
        lstvwResolveFunctions.setItems(null);
    }

    public void buttonGenerateClick(MouseEvent mouseEvent) {
        if(!(txtfldClassCount.getText().isEmpty() || txtfldClassObjectsCount.getText().isEmpty())){
            int classCount=Integer.parseInt(txtfldClassCount.getText());
            int objectsCount=Integer.parseInt(txtfldClassObjectsCount.getText());
            if (checkInputValues(classCount,2,20,"Количество классов") &&
                    checkInputValues(objectsCount,1,50,"Количество объектов класса")){
                showListViewResults(lstvwLearningSample,classificationDeterminant.getLearningSample(classCount,objectsCount,minAttributeValue,maxAttributeValue));
                btnClassify.setDisable(false);
                setAttributesState(true);
                lstvwFuncValues.setItems(null);
            }
        }else{
            showAlert(Alert.AlertType.ERROR,"Ошибка","Некорректный ввод","Заполните все поля");
        }
    }

    public void buttonClassifyClick(MouseEvent mouseEvent) {
        setAttributesState(false);
        showListViewResults(lstvwResolveFunctions,classificationDeterminant.getResolveFunctions());
    }

    public void buttonDetermineClassClick(MouseEvent mouseEvent) {
        if(!(txtfldFirstAttribute.getText().isEmpty() || txtfldSecondAttribute.getText().isEmpty())){
            int firstAttribute=Integer.parseInt(txtfldFirstAttribute.getText());
            int secondAttribute=Integer.parseInt(txtfldSecondAttribute.getText());
            if (checkInputValues(firstAttribute,minAttributeValue,maxAttributeValue,"Значение атрибута") &&
                    checkInputValues(secondAttribute,minAttributeValue,maxAttributeValue,"Значение атрибута")){
                showListViewResults(lstvwFuncValues,classificationDeterminant.determineObjectClass(firstAttribute,secondAttribute));
            }
        }else{
            showAlert(Alert.AlertType.ERROR,"Ошибка","Некорректный ввод","Заполните все поля");
        }
    }

    public void textFieldKeyPressed(KeyEvent keyEvent) {
        currentTextFieldValue=((TextField)keyEvent.getSource()).getText();
    }

    public void textFieldKeyReleased(KeyEvent keyEvent) {
        if(!((TextField)keyEvent.getSource()).getText().equals(currentTextFieldValue)){
            if(!(keyEvent.getSource().equals(txtfldFirstAttribute) || keyEvent.getSource().equals(txtfldSecondAttribute))) {
                btnClassify.setDisable(true);
                setAttributesState(true);
                lstvwLearningSample.setItems(null);
            }
            lstvwFuncValues.setItems(null);
        }
    }
}
