/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ci_eletronico;

import ci_eletronico.entities.TbUnidadeOrganizacional;
import ci_eletronico.entities.TbUsuario;
import ci_eletronico.utilitarios.Seguranca;
import ci_eletronico_queries.LoginQuery;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
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
import javafx.scene.control.Hyperlink;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * FXML Controller class
 *
 * @author victorcmaf
 */
//public class LoginController implements Initializable {
public class LoginController {
    @FXML
    Label lblMessage;
    @FXML
    private Button btnFechar;
    @FXML
    private Hyperlink hlink;
    @FXML
    private Label lblLogin;
    @FXML
    private TextField txtUsername;
    @FXML
    private Label lblSenha;
    @FXML
    private PasswordField pwdSenha;
    @FXML
    private Button btnAcessar;
    @FXML
    private Label lblUO;
    @FXML
    private ComboBox cmbUO; 
    @FXML
    private Button btnOK;
   

      
    
    
    
    private List<ci_eletronico.entities.TbUsuario> listaUsuarios = new ArrayList<>();
    private List<ci_eletronico.entities.TbUsuarioPerfilUo> listaUO = new ArrayList<>();
    private List<ci_eletronico.entities.TbUnidadeOrganizacionalGestor> listaUOGestor = new ArrayList<>();
    private List<Object[]> listaJoin; 
    private LoginQuery consulta_TB_USUARIO  = new LoginQuery();  
    private LoginQuery consulta_TB_USUARIO_PERFIL_UO  = new LoginQuery();  
    private LoginQuery consulta_TB_UO_GESTOR  = new LoginQuery();  
    
    ObservableList<String> ol_listUO = FXCollections.observableArrayList();
    
    private Scene scene;
    
    private String strIdUsuario = "";
    private String strUsername = "";
    private String strIdUO = "";
    private String strNomeUO = "";
    private String strIdUsuarioPerfil = "";
    private String strDescricaoPerfil = "";
    private String strHtmlAssinatura = "";
    private int nIdUOGestor = 0;
    private String strgUserLogin = "";
    //private LoginQuery consulta  = new LoginQuery();
    /**
     * Initializes the controller class.
     */
//    @Override
//    public void initialize(URL url, ResourceBundle rb) {
  public void initialize() {
        // TODO
        //listaUsuarios = consulta.listaTbUsuario();
//        hlink.setOnAction(new EventHandler() {
//
//            @Override
//            public void handle(Event event) {
//                app.getHostServices().showDocument("http://www.jumpingbean.biz");
//            }
//        });
        boolean blPrecisaUpdate = false;
        
                 
        
        btnAcessar.setDefaultButton(true);        
        
        blPrecisaUpdate = VerificarVersaoSistema();
        if (blPrecisaUpdate){
            //Disabled os labels e botões
            btnAcessar.setVisible(false);
            lblUO.setVisible(false);
            cmbUO.setVisible(false);
            btnOK.setVisible(false); 
            hlink.setVisible(false);
            btnFechar.setVisible(true);
            txtUsername.setVisible(false);
            pwdSenha.setVisible(false);
            lblLogin.setVisible(false);
            lblSenha.setVisible(false);
            lblMessage.setText("");
            lblMessage.setText("Sistema CI-eletrônico precisa ser atualizado.\nFavor entrar em contato com suporte ASSTI. ");
            lblMessage.setVisible(true);
            
        } else {
            ComponentesVisible(false);
            btnFechar.setVisible(false);
            lblMessage.setVisible(false);
            lblMessage.setText(""); 
            hlink.setVisible(false);
            
        }
        
    } 
    
    private boolean VerificarVersaoSistema(){
        boolean blPrecisaUpdate = false;
        String strVersaoBancoDados = "";
        String strVersaoCodigo = "";
        
        strVersaoCodigo = "1.1";
        
        LoginQuery consulta_TB_ATUALIZAR_SISTEMA  = new LoginQuery(); 
        List<ci_eletronico.entities.TbAtualizarSistema> listaVersoes = new ArrayList<>();
        listaVersoes = consulta_TB_ATUALIZAR_SISTEMA.listaTbAtualizarSistema();
        for(ci_eletronico.entities.TbAtualizarSistema l : listaVersoes){
            blPrecisaUpdate = l.getAtsiPrecisaAtualizar();
            strVersaoBancoDados = l.getAtsiVersao();
        }
        if (blPrecisaUpdate){
            if (0==strVersaoCodigo.compareTo(strVersaoBancoDados)){
                blPrecisaUpdate = false;
            } else {
                blPrecisaUpdate = true;
            }
        }
        return blPrecisaUpdate;
    }
    
    @FXML
    private void handleBtnFecharAplicacao(ActionEvent event){
        //Ocultamos a janela de login
        (((Node)event.getSource()).getScene()).getWindow().hide();
        //--------- FIM Ocultar janela de Login ------------
    }
    
    @FXML
    private void handleBtnAcessarAction(ActionEvent event) throws IOException, Exception {
        
        
        //FXMLLoader loader = new FXMLLoader();
        
        //FXMLMainController mainController = 
        
        String strUserLogin ="";
        strUserLogin = txtUsername.getText();
        String strlUOGestorDescricao = "";
        
        listaUsuarios = consulta_TB_USUARIO.listaUserLogin(strUserLogin);
        
        if (listaUsuarios.size()>0){
            for(ci_eletronico.entities.TbUsuario l : listaUsuarios){
                if(txtUsername.getText().equals(l.getUsuLogin())){

                    String strPassword = pwdSenha.getText();
                    String strEnc = Seguranca.encriptar(strPassword);
                    String strMD5 = Seguranca.stringToMD5(strPassword);
                    
                    //if(strEnc.equals(l.getUsuSenha())){ // Utilizamos Encriptação
                    if(strMD5.equals(l.getUsuSenha().toUpperCase())){ //Utilizamos MD5
                        this.strgUserLogin = strUserLogin;
                        ComponentesVisible(true);
                        ComponentesDisable(true);
                        btnAcessar.setDefaultButton(false);
                        btnOK.setDefaultButton(true);
                        //Verificamos qual UO faz parte
                        TbUsuario nIdUsuario = new TbUsuario(l.getIdUsuario()); //= 0;
                                                
                        //Getting assinatura do Usuario
                        strHtmlAssinatura = l.getUsuAssinatura();
                        if (null == strHtmlAssinatura){
                            strHtmlAssinatura = "";
                        }
                                                
                        String strUO = "";
                        //nIdUsuario.setIdUsuario(l.getIdUsuario());
                        strIdUsuario = l.getIdUsuario().toString();
                        strUsername = l.getUsuNomeCompleto();
                        
                        
                        listaUO = consulta_TB_USUARIO_PERFIL_UO.listaUO(nIdUsuario);   
                        //listaUO = consulta_TB_USUARIO_PERFIL_UO.listaJoinUO2(nIdUsuario);  
                         for(ci_eletronico.entities.TbUsuarioPerfilUo lUO : listaUO){
//                             System.out.println("TbUsuarioPerfilUo campo 1 - " + lUO.getIdUnidadeOrganizacional().getIdUnidadeOrganizacional());
//                             System.out.println("TbUsuarioPerfilUo campo 2 - " + lUO.getIdUnidadeOrganizacional().getUnorNome());
//                             System.out.println("TbUsuarioPerfilUo campo 3 - " + lUO.getIdUsuarioPerfil().getIdUsuarioPerfil());
//                             System.out.println("TbUsuarioPerfilUo campo 1 - " + lUO.getIdUsuarioPerfil().getPeusDescricao());
                             cmbUO.getItems().add(lUO.getIdUnidadeOrganizacional().getIdUnidadeOrganizacional() + " - " + lUO.getIdUnidadeOrganizacional().getUnorNome() + " ; " + lUO.getIdUsuarioPerfil().getIdUsuarioPerfil()+" - "+lUO.getIdUsuarioPerfil().getPeusDescricao());
                         }
                       
                        
                        cmbUO.getSelectionModel().selectFirst(); 
                        //TbUnidadeOrganizacional nIdUO;
                        
                        //Variaveis para sber o nome da UO Gestora
                        List<ci_eletronico.entities.TbUnidadeOrganizacional> listaUODescricao = new ArrayList<>();
                        LoginQuery consulta_Nome_UO  = new LoginQuery(); 
                        //----------------------------------------------------------
                        
                        if (listaUO.size()>0 && listaUO.size() < 2) {
                        //if (listaUO.size()>0 && listaUO.size() < 2){
                            strUO = cmbUO.getSelectionModel().getSelectedItem().toString();
//                            System.out.println("Valor do combobox selecionado: " + strUO);
                            TbUnidadeOrganizacional nIdUO;
                            
                            for(ci_eletronico.entities.TbUsuarioPerfilUo lUO : listaUO){   
                                nIdUO = new TbUnidadeOrganizacional(lUO.getIdUnidadeOrganizacional().getIdUnidadeOrganizacional());
                                strIdUO = lUO.getIdUnidadeOrganizacional().getIdUnidadeOrganizacional().toString();
                                strNomeUO = lUO.getIdUnidadeOrganizacional().getUnorNome();
                                strIdUsuarioPerfil = lUO.getIdUsuarioPerfil().getIdUsuarioPerfil().toString();
                                strDescricaoPerfil = lUO.getIdUsuarioPerfil().getPeusDescricao();                             
                                listaUOGestor = consulta_TB_UO_GESTOR.getIdUOGestor(nIdUO);
                            } 
                            for (ci_eletronico.entities.TbUnidadeOrganizacionalGestor lUOGestor: listaUOGestor){
                                nIdUOGestor = lUOGestor.getIdUoGestor();                                  
                            }
                            
                            listaUODescricao = consulta_Nome_UO.getDescricaoUOGestor(nIdUOGestor); 
                            for (ci_eletronico.entities.TbUnidadeOrganizacional lista: listaUODescricao){
                               strlUOGestorDescricao = lista.getUnorNome();
                            }
                            
                            //loader.setLocation(FXMLMainController.class.getResource("/ci_eletronico/FXMLMain.fxml"));
                                                      
                            //Ocultamos a janela de login
                            (((Node)event.getSource()).getScene()).getWindow().hide();
                            //--------- FIM Ocultar janela de Login ------------
                            
//                                                       
                            //Mostramos MainWindow com dados do usuário logado
                            ShowMainWindowCIe(this, strIdUsuario, strUsername, strIdUO, strNomeUO, strIdUsuarioPerfil, strDescricaoPerfil, strHtmlAssinatura, nIdUOGestor, strgUserLogin,
                                    strlUOGestorDescricao);

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
//                               ol_listUO.add(lUO.getIdUsuario()+ " - " + lUO.getIdUnidadeOrganizacional() + " - " + lUO.getIdUsuarioPerfil());                               
//                               str = ol_listUO.get(n);
//                               str = ol_listUO.toString();
//                               n++;
                               //cmbUO.getItems().add(lUO.getIdUnidadeOrganizacional().getIdUnidadeOrganizacional() + "-" + lUO.getUnorNome().getUnorNome() + "-" + lUO.getIdUsuarioPerfil().getIdUsuarioPerfil()+"-"+lUO.getPeusDescricao().getPeusDescricao());
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
        
        String strcmbUO;    
        TbUnidadeOrganizacional nIdUO;        
        
        strcmbUO = cmbUO.getSelectionModel().getSelectedItem().toString();
        
        System.out.println("Valor do combobox selecionado: " + strcmbUO);
        //String delimiters = "\\s+|;\\s*|\\-\\s*";
        String delimiters = "-|\\;";
        String[] parts = strcmbUO.split(delimiters);
        strIdUO = parts[0].trim(); // 004
        strNomeUO = parts[1].trim(); // 034556
        strIdUsuarioPerfil = parts[2].trim();
        strDescricaoPerfil = parts[3].trim();        
        for (String token: parts){
            System.out.println(token);
        }
        
        nIdUO = new TbUnidadeOrganizacional(Integer.parseInt(strIdUO));
        listaUOGestor = consulta_TB_UO_GESTOR.getIdUOGestor(nIdUO);
        
        for (ci_eletronico.entities.TbUnidadeOrganizacionalGestor lUOGestor: listaUOGestor){
            nIdUOGestor = lUOGestor.getIdUoGestor();
        }
        
        List<ci_eletronico.entities.TbUnidadeOrganizacional> listaUODescricao = new ArrayList<>();
        LoginQuery consulta_Nome_UO  = new LoginQuery();         
        listaUODescricao = consulta_Nome_UO.getDescricaoUOGestor(nIdUOGestor); 
        String strlUOGestorDescricao = "";
        
        for (ci_eletronico.entities.TbUnidadeOrganizacional lista: listaUODescricao){
           strlUOGestorDescricao = lista.getUnorNome();
        }
        
         //Ocultamos a janela de login
        (((Node)event.getSource()).getScene()).getWindow().hide();
        //--------- FIM Ocultar janela de Login ------------
        
        ShowMainWindowCIe(this, strIdUsuario, strUsername, strIdUO, strNomeUO, strIdUsuarioPerfil, strDescricaoPerfil, strHtmlAssinatura, nIdUOGestor, strgUserLogin, 
                strlUOGestorDescricao );
      
        
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
    
    private void ShowMainWindowCIe(final LoginController loginController , String strIdUsuario, String strNomeUsuario, 
                                        String strIdUO, String strNomeUO, String strIdPerfil, String strDescricaoPerfil, String strHtmlAssinatura, int nIdUOGestor, 
                                        String strlUserLogin, String strlUOGestorDescricao){
        try{
            
                scene = new Scene(new BorderPane());
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ci_eletronico/FXMLMain.fxml"));
                scene.setRoot((Parent) loader.load());
                FXMLMainController controller = loader.<FXMLMainController>getController();
                controller.setVariaveisAmbiente(loginController,strIdUsuario,strNomeUsuario,strIdUO,strNomeUO,strIdPerfil,strDescricaoPerfil, strHtmlAssinatura, 
                        nIdUOGestor, strlUserLogin, strlUOGestorDescricao);

                Stage stage = new Stage();
                stage.setTitle("CI-eletrônico");
                //set icon
                stage.getIcons().add(new Image("/resources/CI_FX02.png"));
                
                scene.getStylesheets().add(getClass().getResource("/resources/highlightingTable.css").toExternalForm());

                stage.setScene(scene);
                stage.show();
//                                
            }catch (IOException ex) {
                Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    
}
