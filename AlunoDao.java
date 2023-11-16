package com.exemplocrud;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;

public class AlunoDao {

    private Conexao conexao;
    private SQLiteDatabase banco;


    //context é usado para a conexão
    public AlunoDao(Context context){
        conexao = new Conexao(context); //criei uma conexao
        banco = conexao.getWritableDatabase(); //iniciar um banco de dados para escrita
    }

    //método para inserir - PARTE I
    /*public long inserir(Aluno aluno){ // long porque retorna o id do aluno
        ContentValues values = new ContentValues(); //valores que irei inserir
        values.put("nome", aluno.getNome());
        values.put("cpf", aluno.getCpf());
        values.put("telefone", aluno.getTelefone());
        return banco.insert("aluno",null, values); //tabela aluno, não tera colunas vazias, valores values
    }*/

    public long inserir(Aluno aluno){ // long porque retorna o id do aluno
        if (!cpfExistente(aluno.getCpf())) {
            ContentValues values = new ContentValues(); //valores que irei inserir
            values.put("nome", aluno.getNome());
            values.put("cpf", aluno.getCpf());
            values.put("telefone", aluno.getTelefone());
            values.put("foto_bytes", aluno.getFotoBytes()); // FOTO
            return banco.insert("aluno",null, values); //tabela aluno, não tera colunas vazias, valores values
         }
        else{
            // CPF já existe, você pode lidar com isso de acordo com sua lógica
            return -1; // Retorno -1 para chamada do método inserir() no CadastroAlunoActivity
        }
    }

    //ATUALIZAR PARTE VI
    public void atualizar(Aluno aluno){
            ContentValues values = new ContentValues(); //valores que irei inserir
            values.put("nome", aluno.getNome());
            values.put("cpf", aluno.getCpf());
            values.put("telefone", aluno.getTelefone());
            values.put("foto_bytes", aluno.getFotoBytes()); // FOTO
            banco.update("aluno", values, "id = ?", new String[]{aluno.getId().toString()});
    }


    //VERIFICA SE O CPF EXISTE NO BANCO DE DADOS
    private boolean cpfExistente(String cpf) {
        // Consulta no banco de dados para verificar se o CPF já existe
        Cursor cursor = banco.query("aluno", new String[]{"id"}, "cpf = ?", new String[]{cpf}, null, null, null);
        boolean cpfExiste = cursor.getCount() > 0;
        cursor.close();
        return cpfExiste;
    }


    //método para consultar PARTE II
    public List<Aluno> obterTodos(){
        List<Aluno> alunos = new ArrayList<>();
        //cursor aponta para as linhas retornadas da tabela
        Cursor cursor = banco.query("aluno", new String[]{"id", "nome", "cpf", "telefone", "foto_bytes"},
                null, null,null,null,null); //nome da tabela, nome das colunas, completa com null o método
        //que por padrão pede esse número de colunas obrigatórias
        while(cursor.moveToNext()){ //verifica se consegue mover para o próximo ponteiro ou linha
            Aluno a = new Aluno();
            a.setId(cursor.getInt(0)); // new String[]{"id", "nome", "cpf", "telefone"}, id é coluna '0'
            a.setNome(cursor.getString(1)); // new String[]{"id", "nome", "cpf", "telefone"}, nome é coluna '1'
            a.setCpf(cursor.getString(2)); // new String[]{"id", "nome", "cpf", "telefone"}, cpf é coluna '2'
            a.setTelefone(cursor.getString(3)); // new String[]{"id", "nome", "cpf", "telefone"}, telefone é coluna '3'
            a.setFotoBytes(cursor.getBlob(4)); // Adicione esta linha - FOTO
            alunos.add(a);
        }
        return alunos;
    }

    //METODO EXCLUIR
    public void excluir(Aluno a){
        banco.delete("aluno", "id = ?",new String[]{a.getId().toString()}); // no lugar do ? vai colocar o id do aluno
    }

        //VALIDAR CPF
    public boolean validaCpf(String CPF) {

        // Código adaptado de: https://receitasdecodigo.com.br/java/classe-java-completa-para-gerar-e-validar-cpf-e-cnpj

        System.out.println("String de entrada do metodo: " + CPF); //DEBUG

        // considera-se erro CPF's formados por uma sequencia de numeros iguais
        if (CPF.equals("00000000000") || CPF.equals("11111111111") || CPF.equals("22222222222") ||
                CPF.equals("33333333333") || CPF.equals("44444444444") || CPF.equals("55555555555") ||
                CPF.equals("66666666666") || CPF.equals("77777777777") || CPF.equals("88888888888") ||
                CPF.equals("99999999999") || (CPF.length() != 11))
            return (false);

        char dig10, dig11;
        int sm, i, r, num, peso;

        // "try" - protege o codigo para eventuais erros de conversao de tipo (int)
        try {
            // Calculo do 1o. Digito Verificador
            sm = 0;
            peso = 10;
            for (i = 0; i < 9; i++) {
                // converte o i-esimo caractere do CPF em um numero:
                // por exemplo, transforma o caractere '0' no inteiro 0
                // (48 eh a posicao de '0' na tabela ASCII)
                num = (int) (CPF.charAt(i) - 48);
                sm = sm + (num * peso);
                peso = peso - 1;
            }

            r = 11 - (sm % 11);
            if ((r == 10) || (r == 11))
                dig10 = '0';
            else
                dig10 = (char) (r + 48); // converte no respectivo caractere numerico

            // Calculo do 2o. Digito Verificador
            sm = 0;
            peso = 11;
            for (i = 0; i < 10; i++) {
                num = (int) (CPF.charAt(i) - 48);
                sm = sm + (num * peso);
                peso = peso - 1;
            }

            r = 11 - (sm % 11);
            if ((r == 10) || (r == 11))
                dig11 = '0';
            else
                dig11 = (char) (r + 48);

            // Verifica se os digitos calculados conferem com os digitos informados.
            if ((dig10 == CPF.charAt(9)) && (dig11 == CPF.charAt(10)))
                return (true);
            else
                return (false);
        } catch (InputMismatchException erro) {
            return (false);
        }
    }


}
