/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ci_eletronico;

import ci_eletronico.entities.TbUsuario;
import ci_eletronico.utilitarios.Seguranca;
import ci_eletronico_queries.LoginQuery;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author victorcmaf
 */
public class LoginController implements Initializable {
    @FXML
    private Button btnAcessar;
    @FXML
    private Button btnOK;
    @FXML
    private TextField txtUsername;
    @FXML
    private PasswordField pwdSenha;
    @FXML
    private ComboBox cmbUO;
    @FXML
    private Label lblUO;
    
    private List<ci_eletronico.entities.TbUsuario> listaUsuarios = new ArrayList<>();
    private List<ci_eletronico.entities.TbUsuarioPerfilUo> listaUO = new ArrayList<>();
    private List<Object[]> listaJoin; 
    private LoginQuery consulta_TB_USUARIO  = new LoginQuery();  
    private LoginQuery consulta_TB_USUARIO_PERFIL_UO  = new LoginQuery();  
    
    ObservableList<String> ol_listUO = FXCollections.observableArrayList();
    //private LoginQuery consulta  = new LoginQuery();
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        //listaUsuarios = consulta.listaTbUsuario();
        ComponentesVisible(false);            
        
        btnOK.setDefaultButton(true);
    } 
    
    @FXML
    private void handleBtnAcessarAction(ActionEvent event) throws IOException, Exception {
        
        FXMLMainController mainController = new FXMLMainController();
        
        String strUserLogin ="";
        strUserLogin = txtUsername.getText();
        listaUsuarios = consulta_TB_USUARIO.listaUserLogin(strUserLogin);
        
        if (listaUsuarios.size()>0){
            for(ci_eletronico.entities.TbUsuario l : listaUsuarios){
                if(txtUsername.getText().equals(l.getUsuLogin())){

                    String strPassword = pwdSenha.getText();
                    String strEnc = Seguranca.encriptar(strPassword);
                    //String strDec = Seguranca.desencriptar(strEnc);

                    //if(pwdSenha.getText().equals(l.getUsuSenha())){
                    if(strEnc.equals(l.getUsuSenha())){
                        ComponentesVisible(true);
                        ComponentesDisable(true);
                        //Verificamos qual UO faz parte
                        TbUsuario nIdUsuario = new TbUsuario(l.getIdUsuario()); //= 0;
                        String strUsername = "";
                        //nIdUsuario.setIdUsuario(l.getIdUsuario());
                        strUsername = l.getUsuNomeCompleto();
                        mainController.setStrIdUsuario(l.getIdUsuario().toString());
                        mainController.setStrNomeUsuario(strUsername);
                        
                        listaUO = consulta_TB_USUARIO_PERFIL_UO.listaUO(nIdUsuario);   
                        listaJoin = consulta_TB_USUARIO_PERFIL_UO.listaJoinUO(nIdUsuario);
                        if (listaUO.size()>0 && listaUO.size() < 2){
                            
                            //Ocultamos a janela de login
                            (((Node)event.getSource()).getScene()).getWindow().hide();


                            //Mostramos uma nova janela chamada MainWindow
                            Parent parent;

                            parent = FXMLLoader.load(getClass().getResource("/ci_eletronico/FXMLMain.fxml"));


                            Scene scene = new Scene(parent);
                            scene.setUserData(mainController);
                            
                            Stage stage = new Stage();
                            stage.setTitle("CI-eletrônico");
                            //set icon
                            stage.getIcons().add(new Image("/resources/CI_FX02.png"));

                            stage.setScene(scene);
                            stage.show();
                        } else {
                            // Show the error message.
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Informação de UOs");
                            alert.setHeaderText("Usuário possui mais de uma UO");
                            alert.setContentText("Por favor selecione só uma UO do combobox");
                            alert.showAndWait();
                            String str;
                            int n = 0;
                            
                            for(ci_eletronico.entities.TbUsuarioPerfilUo lUO : listaUO){
                               ol_listUO.add(lUO.getIdUsuario()+ " - " + lUO.getIdUnidadeOrganizacional() + " - " + lUO.getIdUsuarioPerfil());                               
                               str = ol_listUO.get(n);
                               str = ol_listUO.toString();
                               n++;
                               cmbUO.getItems().add(lUO.getIdUsuario().getIdUsuario()+ "-"+ lUO.getIdUnidadeOrganizacional().getIdUnidadeOrganizacional()+ "-" + lUO.getIdUsuarioPerfil().getIdUsuarioPerfil());
                            }
//                            cmbUO.getItems().addAll(listaUO.g)
//                            cmbUO.setItems(ol_listUO);                            
                            
                        }
                    } else {
                        // Show the error message.
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText("Senha errada");
                        alert.setContentText("Por favor preencha o campo com a senha correta");
                        alert.showAndWait();  
                        
                    }

                } else {
                    // Show the error message.
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Login errado");
                    alert.setContentText("Por favor preencha o campo com o Login correto");
                    alert.showAndWait();
                }
            }
        } else {
            // Show the error message.
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Login não encontrado");
            alert.setContentText("Por favor preencha o campo com o Login correto");
            alert.showAndWait();
        }
            
        
    }
    
    @FXML
    private void handleBtnOKAction(ActionEvent event) throws IOException {
        
    }
    private void ComponentesVisible(boolean bCondicao){
        lblUO.setVisible(bCondicao);
        cmbUO.setVisible(bCondicao);
        btnOK.setVisible(bCondicao);  
        
    } 
    private void ComponentesDisable(boolean bCondicao){
        txtUsername.setDisable(bCondicao);
        pwdSenha.setDisable(bCondicao);
        btnAcessar.setDisable(bCondicao);
    }
    
}
