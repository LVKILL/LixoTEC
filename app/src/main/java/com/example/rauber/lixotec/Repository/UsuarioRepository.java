package com.example.rauber.lixotec.Repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.LiveDataReactiveStreams;
import android.arch.lifecycle.MediatorLiveData;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.rauber.lixotec.DAO.AdministradorDAO;
import com.example.rauber.lixotec.DAO.EmpresaDAO;
import com.example.rauber.lixotec.DAO.PessoaDAO;
import com.example.rauber.lixotec.DAO.UsuarioDAO;
import com.example.rauber.lixotec.Database.LixoTecDatabase;
import com.example.rauber.lixotec.Model.Administrador;
import com.example.rauber.lixotec.Model.Empresa;
import com.example.rauber.lixotec.Model.Pessoa;
import com.example.rauber.lixotec.Model.Usuario;
import com.example.rauber.lixotec.RetrofitAPIs.LixotecAPI;
import com.example.rauber.lixotec.RetrofitAPIs.RetroClass;
import com.example.rauber.lixotec.SharedPreferences.SystemPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class UsuarioRepository {

    private UsuarioDAO usuarioDAO;
    private EmpresaDAO empresaDAO;
    private PessoaDAO pessoaDAO;
    private AdministradorDAO administradorDAO;
    private Usuario usuario;
    private Pessoa usuarioPessoa;
    private Empresa usuarioEmpresa;
    private Administrador usuarioAdministrador;
    private RetroClass retroClass;
    private LiveData<List<Usuario>> allUser;
    private MediatorLiveData<Pessoa> pessoaMediatorLiveData = new MediatorLiveData<>();
    private MediatorLiveData<Usuario> usuarioMediatorLiveData = new MediatorLiveData<>();
    private MediatorLiveData<Empresa> empresaMediatorLiveData = new MediatorLiveData<>();
    private MediatorLiveData<String> checkUsername = new MediatorLiveData<>();
    private MediatorLiveData<String> authUser = new MediatorLiveData<>();
    private MediatorLiveData<String> registerResponse = new MediatorLiveData<>();

    private LixotecAPI lixotecAPI;
    private SystemPreferences systemPreferences;

    public UsuarioRepository(Application application){
        LixoTecDatabase lixoTecDatabase = LixoTecDatabase.getInstance(application);
        systemPreferences = new SystemPreferences(application.getApplicationContext());
        usuarioDAO = lixoTecDatabase.usuarioDAO();
        empresaDAO = lixoTecDatabase.empresaDAO();
        pessoaDAO = lixoTecDatabase.pessoaDAO();
        administradorDAO = lixoTecDatabase.administradorDAO();
        allUser = usuarioDAO.allUsers();
        retroClass = new RetroClass();
        lixotecAPI = retroClass.getRetroClass().create(LixotecAPI.class);
    }

    public void checkUsername(String json) {
        final LiveData<String> source = LiveDataReactiveStreams.fromPublisher(
                lixotecAPI.consultarUsername(json)
                .subscribeOn(Schedulers.io())
                .onErrorReturn(throwable -> createErrorJson())
        );

        checkUsername.addSource(source, new android.arch.lifecycle.Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                if( s != null ){
                    checkUsername.setValue(s);
                    checkUsername.removeSource(source);
                }
            }
        });
    }

    public LiveData<String> getCheckUsername(){
        return this.checkUsername;
    }

    public void registerWithJson(String json){
        final LiveData<String> source = LiveDataReactiveStreams.fromPublisher(
                lixotecAPI.registrarUsuario(json)
                .subscribeOn(Schedulers.io())
                .onErrorReturn(throwable -> createErrorJson())
        );

        registerResponse.addSource(source, new android.arch.lifecycle.Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                String response = convertJsonResponse(s);
                registerResponse.setValue(response);
                registerResponse.removeSource(source);
            }
        });
    }

    public void loadUserCompany(int id){
        final LiveData<Empresa> source = LiveDataReactiveStreams.fromPublisher(
                empresaDAO.getEmpresaById(id)
                .subscribeOn(Schedulers.io())
        );

        empresaMediatorLiveData.addSource(source, new android.arch.lifecycle.Observer<Empresa>() {
            @Override
            public void onChanged(@Nullable Empresa empresa) {
                if(empresa != null){
                    empresaMediatorLiveData.setValue(empresa);
                    empresaMediatorLiveData.removeSource(source);
                }
            }
        });

    }

    public LiveData<Empresa> getCompany(){
        return this.empresaMediatorLiveData;
    }

    public void loadUserPerson(int id){
        final LiveData<Pessoa> source = LiveDataReactiveStreams.fromPublisher(
                pessoaDAO.getUserById(id)
                .subscribeOn(Schedulers.io())
        );

        pessoaMediatorLiveData.addSource(source, new android.arch.lifecycle.Observer<Pessoa>() {
            @Override
            public void onChanged(@Nullable Pessoa pessoa) {
                if(pessoa != null){
                    pessoaMediatorLiveData.setValue(pessoa);
                    pessoaMediatorLiveData.removeSource(source);
                }
            }
        });
    }

    public LiveData<Pessoa> getUserPessoaById(){
        return pessoaMediatorLiveData;
    }

    public void loadUser(int id){
        final LiveData<Usuario> source = LiveDataReactiveStreams.fromPublisher(
                usuarioDAO.getUserById(id)
                .subscribeOn(Schedulers.io())

        );

        usuarioMediatorLiveData.addSource(source, new android.arch.lifecycle.Observer<Usuario>() {
            @Override
            public void onChanged(@Nullable Usuario usuario) {
                if(usuario != null){
                    Log.v("tag","dado live "+usuario.getEmail());
                    usuarioMediatorLiveData.setValue(usuario);
                    usuarioMediatorLiveData.removeSource(source);
                }
            }
        });
    }

    public LiveData<Usuario> getUser(){
        return usuarioMediatorLiveData;
    }

    public LiveData<String> observeRegisterResponse(){
        return registerResponse;
    }

    private String convertJsonResponse(String json){
        String response = "";

        try{
            JSONArray jsonArray = new JSONArray(json);
            for ( int i = 0 ; i < jsonArray.length() ; i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String respostaJson = jsonObject.getString("resposta");
                response = respostaJson;
            }
        }catch(JSONException je){
            Log.v("tag","erro json "+je);
            Log.v("tag","json recebido "+json);
        }

        return response;
    }

    public void authenticateWithJson(String json){
        final LiveData<String> source = LiveDataReactiveStreams.fromPublisher(
                lixotecAPI.realizarLogin(json)
                .subscribeOn(Schedulers.io())
                .onErrorReturn(throwable -> createErrorJson())
        );

        authUser.addSource(source, new android.arch.lifecycle.Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                    convertJsonUser(s);
                    authUser.setValue(s);
                    authUser.removeSource(source);
            }
        });
    }

    public LiveData<String> observeLogin(){
        return authUser;
    }

    private void convertJsonUser(String json){

        try{
            Log.v("tag",""+json);
            JSONArray jsonArray = new JSONArray(json);
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            String userType = jsonObject.getString("tipo_usuario");

            if(userType.equals("pessoa")){

                int idUsuario = Integer.parseInt(jsonObject.getString("id_usuario"));
                int idPessoa = Integer.parseInt(jsonObject.getString("id_pessoa"));
                String nome = String.valueOf(jsonObject.getString("nome_completo"));
                String cpf = String.valueOf(jsonObject.getString("cpf"));
                String login = String.valueOf(jsonObject.getString("login"));
                String senha = String.valueOf(jsonObject.getString("senha"));
                String email = String.valueOf(jsonObject.getString("email"));
                String telefone = String.valueOf(jsonObject.getString("telefone"));
                this.usuario = new Usuario(idUsuario, login, senha, email, telefone, 2);
                this.usuarioPessoa = new Pessoa(idPessoa, nome, cpf, idUsuario);
                systemPreferences.removePreferences();
                systemPreferences.setUserUd(idUsuario);
                systemPreferences.setUserEmail(email);
                systemPreferences.setUserType(2);
                systemPreferences.setAlreadyLogged(true);
                systemPreferences.setUserName(nome);
                insertUser(usuario);

            }else if(userType.equals("empresa")){

                int idUsuario = Integer.parseInt(String.valueOf(jsonObject.getString("id_usuario")));
                int idEmpresa = Integer.parseInt(String.valueOf(jsonObject.getString("id_empresa")));
                String nomeFantasia = String.valueOf(jsonObject.getString("nome_fantasia"));
                String razaoSocial = String.valueOf(jsonObject.getString("razao_social"));
                String cnpj = String.valueOf(jsonObject.getString("cnpj"));
                String login = String.valueOf(jsonObject.getString("login"));
                String senha = String.valueOf(jsonObject.getString("senha"));
                String email = String.valueOf(jsonObject.getString("email"));
                String telefone = String.valueOf(jsonObject.getString("telefone"));
                systemPreferences.removePreferences();
                systemPreferences.setUserUd(idUsuario);
                systemPreferences.setAlreadyLogged(true);
                systemPreferences.setUserEmail(email);
                systemPreferences.setUserType(3);
                systemPreferences.setUserName(nomeFantasia);
                this.usuario = new Usuario(idUsuario, login, senha, email, telefone, 3);
                this.usuarioEmpresa = new Empresa(idEmpresa, cnpj, razaoSocial, nomeFantasia, idUsuario);
                insertUser(usuario);

            }else if(userType.equals("administrador")){

                int idUsuario = Integer.parseInt(String.valueOf(jsonObject.getString("id_usuario")));
                int idAdmin = Integer.parseInt(String.valueOf(jsonObject.getString("id_administrador")));
                boolean isSubAdmin = (Integer.parseInt(String.valueOf(jsonObject.getString("is_sub_administrador"))) == 1);
                String login = String.valueOf(jsonObject.getString("login"));
                String senha = String.valueOf(jsonObject.getString("senha"));
                String email = String.valueOf(jsonObject.getString("email"));
                String telefone = String.valueOf(jsonObject.getString("telefone"));
                systemPreferences.removePreferences();
                systemPreferences.setUserUd(idUsuario);
                systemPreferences.setUserEmail(email);
                systemPreferences.setUserType(1);
                systemPreferences.setAlreadyLogged(true);
                this.usuario = new Usuario(idUsuario, login, senha, email, telefone, 1);
                this.usuarioAdministrador = new Administrador(idAdmin, isSubAdmin, idUsuario);
                insertUser(usuario);

            }

        }catch (JSONException je){
            Log.v("tag",""+je);
            Log.v("tag"," json received on error " +json);
        }

    }

    public void registerUser(String json){

        Retrofit retrofit = retroClass.getRetroClass();
        LixotecAPI lixotecAPI = retrofit.create(LixotecAPI.class);
        lixotecAPI.cadastrarUsuario(json).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()){
                    Log.v("TAG","Sucesso em cadastrar usuário e endereço");
                }else{
                    Log.v("tag",""+response.raw());
                    Log.v("TAG","A requisição funcionou, porém falhou");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.v("tag erro",""+t.getMessage());
            }
        });
    }

    private String createErrorJson() {
        String resultTest = "";

        try{
            JSONArray jsonArray = new JSONArray();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("codigo","600");
            jsonObject.put("mensagem","Erro ao conectar com o webservice");
            jsonObject.put("quantidade", "0");
            jsonArray.put(jsonObject);
            resultTest = jsonArray.toString();
        }catch (JSONException je){
            Log.v("tag","Error in createErrorJson() in ColetaRepository " +je.getMessage());
        }
        return resultTest;
    }

    public void insertUser(Usuario usuario){
        InsertAsyncTask insertAsyncTask = new InsertAsyncTask(usuarioDAO);
        insertAsyncTask.execute(usuario);
    }

    public void insertUserPerson(){
        InsertPersonAsyncTask insertPersonAsyncTask = new InsertPersonAsyncTask(pessoaDAO);
        insertPersonAsyncTask.execute(usuarioPessoa);
    }

    public void insetUserCompany(){
        InsertCompanyAsyncTask insertCompanyAsyncTask = new InsertCompanyAsyncTask(empresaDAO);
        insertCompanyAsyncTask.execute(usuarioEmpresa);
    }

    public void insertUserAdmin(){
        InsertAdminAsyncTask insertAdminAsyncTask = new InsertAdminAsyncTask(administradorDAO);
        insertAdminAsyncTask.execute(usuarioAdministrador);
    }


    public void delete(Usuario usuario){
        DeleteAsyncTask deleteAsyncTask = new DeleteAsyncTask(usuarioDAO);
        deleteAsyncTask.execute(usuario);
    }

    public void updateUser(Usuario usuario){
//        (int idUser, String email, String phone)
//        HashMap hashMap = new HashMap<String, Object>();
//        hashMap.put("idUser", idUser);
//        hashMap.put("email",email);
//        hashMap.put("phone",phone);
//        Log.v("tag","repotag" + hashMap.get("email"));
//        Log.v("tag","repotag" + hashMap.get("phone"));

        UpdateAsyncTask updateAsyncTask = new UpdateAsyncTask(usuarioDAO);
        updateAsyncTask.execute(usuario);
    }

    public void updateUserOnWebservice(String json){
        lixotecAPI.alterarDadosUsuario(json).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.v("tag","successfull response "+response.isSuccessful());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.v("tag","error onFailure() updateUserOnWebservice() "+t.getMessage());
            }
        });
       }

    public LiveData<List<Usuario>> getAllUsuario(){
        return allUser;
    }



    class InsertAsyncTask extends AsyncTask<Usuario, Void, Integer>{

        private UsuarioDAO usuarioDAO;

        public InsertAsyncTask(UsuarioDAO usuarioDAO){
            this.usuarioDAO = usuarioDAO;
        }

        @Override
        protected Integer doInBackground(Usuario... usuarios) {
            usuarioDAO.insertUser(usuarios[0]);
            Log.v("tag","inserindo usuario "+usuarios[0].getEmail()+" "+usuarios[0].getIdUsuario());
            return usuarios[0].getTipoUsuario();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            int userType = (int) integer;
            if(userType == 2){
                insertUserPerson();
                Log.v("tag"," vai inserir usuario pessoa");
            }else if(userType == 3){
                insetUserCompany();
                Log.v("tag"," vai inserir usuario empresa");
            }else if(userType == 1){
                insertUserAdmin();
                Log.v("tag"," vai inserir usuario administrador");
            }
            super.onPostExecute(integer);
        }
    }

    class InsertPersonAsyncTask extends AsyncTask<Pessoa, Void, Void>{
        private PessoaDAO pessoaDAO;

        public InsertPersonAsyncTask(PessoaDAO pessoaDAO) {
            this.pessoaDAO = pessoaDAO;
        }

        @Override
        protected Void doInBackground(Pessoa... pessoas) {
            Log.v("tag"," inserindo usuario Pessoa"+pessoas[0].getNomeCompleto());
            pessoaDAO.insert(pessoas[0]);
            return null;
        }
    }

    class InsertAdminAsyncTask extends AsyncTask<Administrador, Void, Void>{

        private AdministradorDAO administradorDAO;

        public InsertAdminAsyncTask(AdministradorDAO administradorDAO) {
            this.administradorDAO = administradorDAO;
        }

        @Override
        protected Void doInBackground(Administrador... administradors) {
            Log.v("tag","Inserindo usuario administrador");
            this.administradorDAO.insert(administradors[0]);
            return null;
        }
    }

    class InsertCompanyAsyncTask extends AsyncTask<Empresa, Void, Void>{
        private EmpresaDAO empresaDAO;

        public InsertCompanyAsyncTask(EmpresaDAO empresaDAO) {
            this.empresaDAO = empresaDAO;
        }

        @Override
        protected Void doInBackground(Empresa... empresas) {
            Log.v("tag","inserindo usuario empresa"+empresas[0].getNomeFantasia());
            empresaDAO.insert(empresas[0]);
            return null;
        }
    }

    class UpdateAsyncTask extends AsyncTask<Usuario, Void, Void>{

        private UsuarioDAO usuarioDAO;
//        private HashMap hashMap;

        public UpdateAsyncTask(UsuarioDAO usuarioDAO) {
            this.usuarioDAO = usuarioDAO;
//            this.hashMap = hashMap;
        }

        @Override
        protected Void doInBackground(Usuario... voids) {
//                int userId = (Integer) hashMap.get("idUser");
//                String phone = hashMap.get("phone").toString();
//                String email = hashMap.get("email").toString();
//                Log.v("tag","tag do in bck "+email);
//
//            usuarioDAO.updateUser(userId, phone, email);
            usuarioDAO.update(voids[0]);
            return null;
        }
    }

    class DeleteAsyncTask extends AsyncTask<Usuario, Void, Void>{

        private UsuarioDAO usuarioDAO;

        public DeleteAsyncTask(UsuarioDAO usuarioDAO) {
            this.usuarioDAO = usuarioDAO;
        }

        @Override
        protected Void doInBackground(Usuario... usuarios) {
            usuarioDAO.insertUser(usuarios[0]);
            return null;
        }
    }
}