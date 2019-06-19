package com.example.rauber.lixotec.Database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;

import com.example.rauber.lixotec.DAO.AdministradorDAO;
import com.example.rauber.lixotec.DAO.ColetaDAO;
import com.example.rauber.lixotec.DAO.EmpresaDAO;
import com.example.rauber.lixotec.DAO.EnderecoDAO;
import com.example.rauber.lixotec.DAO.PessoaDAO;
import com.example.rauber.lixotec.DAO.UsuarioDAO;
import com.example.rauber.lixotec.Model.Administrador;
import com.example.rauber.lixotec.Model.Coleta;
import com.example.rauber.lixotec.Model.Empresa;
import com.example.rauber.lixotec.Model.Endereco;
import com.example.rauber.lixotec.Model.Item;
import com.example.rauber.lixotec.Model.ItensColeta;
import com.example.rauber.lixotec.Model.Metragem;
import com.example.rauber.lixotec.Model.Pessoa;
import com.example.rauber.lixotec.Model.TipoUsuario;
import com.example.rauber.lixotec.Model.Usuario;

@Database(entities = {
        Administrador.class,
        Coleta.class,
        Empresa.class,
        Endereco.class,
        Item.class,
        ItensColeta.class,
        Metragem.class,
        Pessoa.class,
        TipoUsuario.class,
        Usuario.class},
        version = 6)
public abstract class LixoTecDatabase extends RoomDatabase {

    private static LixoTecDatabase instance;

    public abstract UsuarioDAO usuarioDAO();
    public abstract EnderecoDAO enderecoDAO();
    public abstract ColetaDAO coletaDAO();
    public abstract PessoaDAO pessoaDAO();
    public abstract EmpresaDAO empresaDAO();
    public abstract AdministradorDAO administradorDAO();

    public static synchronized LixoTecDatabase getInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    LixoTecDatabase.class,
                    "lixotec_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(getCallBack())
                    .build();
        }
        return instance;
    }

    private static RoomDatabase.Callback getCallBack(){
        RoomDatabase.Callback rdc = new Callback() {
            @Override
            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                super.onCreate(db);
                db.execSQL("INSERT INTO TipoUsuario(id_tipo_usuario, tipo_usuario) values(1," + "'admin'" + ");");
                db.execSQL("INSERT INTO TipoUsuario(id_tipo_usuario, tipo_usuario) values(2," + "'pessoa'" + ");");
                db.execSQL("INSERT INTO TipoUsuario(id_tipo_usuario, tipo_usuario) values(3," + "'empresa'" + ");");
            }
        };
        return rdc;
    }


}
