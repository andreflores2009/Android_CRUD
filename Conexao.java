package com.exemplocrud;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Conexao extends SQLiteOpenHelper {
    private static final String name = "banco.db";
    private static final int version = 2;

    public Conexao(Context context){
        super(context, name, null, version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table aluno(id integer primary key autoincrement, " +
                "nome varchar(50), cpf varchar(50), telefone varchar(50))");
    }

    // BD FOI ATUALIZADO UMA VEZ, COM O CAMPO 'foto_path' AGORA ALTERADO PARA 'foto_bytes'
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Adicione o código para atualizar a tabela se a versão do banco de dados for alterada
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE aluno ADD COLUMN foto_bytes BLOB");
        }
    }
}
