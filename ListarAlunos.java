package com.exemplocrud;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.exemplocrud.databinding.ActivityListarAlunosBinding;

import java.util.ArrayList;
import java.util.List;

public class ListarAlunos extends AppCompatActivity {

    //ORIGINAL
    private ActivityListarAlunosBinding binding;


    //PARTE II
    //app
    private ListView listView;
    private AlunoDao dao;
    private List<Aluno> alunos;
    private List<Aluno> alunosFiltrados = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // No ListarAlunos
        getSupportActionBar().setTitle(R.string.title_dashboard);


        //ORIGINAL
        binding = ActivityListarAlunosBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_listar_alunos);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        //PARTE II
        //vincular variaveis com os campos do layout
        listView = findViewById(R.id.lista_alunos);
        dao = new AlunoDao(this);
        alunos = dao.obterTodos(); //todos alunos
        alunosFiltrados.addAll(alunos); //adiciona em alunosFiltrados só os alunos que foram consultados

        //ArrayAdapter já vem pronto no android para colocar essa lista de alunos na listview
        ArrayAdapter<Aluno> adaptador = new ArrayAdapter<Aluno>(this, android.R.layout.simple_list_item_1, alunosFiltrados);
        //colocar na listView o adaptador
        listView.setAdapter(adaptador);

        //PARTE V
        //registrar o menu de contexto na view Excluir e Atualizar
        //qdo o listView for pressionado, q é oned tem os nomes listados, então pressionamos um nome opr um tempo e ele vai abrir a opção
        //para excluir ou atualizar
        registerForContextMenu(listView);




        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.navigation_home) {
                    // Finalizar todas as atividades existentes
                    //finish();
                    Intent it = new Intent(ListarAlunos.this, CadastroAlunoActivity.class);
                    startActivity(it);
                    return true;
                } else if (itemId == R.id.navigation_dashboard) {
                    // Lógica para a tela de listar alunos (Dashboard)
                    //finish();
                    Intent it = new Intent(ListarAlunos.this, ListarAlunos.class);
                    startActivity(it);
                    return true;
                } else if (itemId == R.id.navigation_notifications) {
                    // Finalizar todas as atividades existentes
                    return true;
                }
                return false;
            }
        });



    }

        //PARTE III
        public boolean onCreateOptionsMenu(Menu menu){
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_principal, menu);

            // Adicione o ouvinte de clique programaticamente
            MenuItem cadastrarItem = menu.findItem(R.id.menu_cadastrar);
            cadastrarItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    cadastrar();
                    return true;
                }
            });

            //PARTE IV
            //PESQUISAR TESTE
            SearchView sv = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
            //verifica as teclas apertadas nele
            sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    System.out.println("Digitou"+ s );
                    procuraAluno(s);
                    return false;
                }
            });

           return true;
        }

        //METODOS------------------------------------------------------------


        //PARTE VI MENU ATUALIZAR
        /*vai ser executado da mesma forma que o excluir, quando ni listar aluno o usuário clicar em cima do nome
        ou segurar opr um tempo em cima do nome, irá aparecer o menu 'ATUALIZAR' na forma de botão*/
        public void atualizar(MenuItem item){
            //mesma lógica do excluir porque o botão de menu é o mesmo
            //pegar qual a posicao do item da lista que eu selecionei para atualizar
            AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            final Aluno alunoAtualizar = alunosFiltrados.get(menuInfo.position);
            //Ao selecionar atualizar, abrir a janela de cadastro e enviar esse aluno para lá
            Intent it = new Intent(this, CadastroAlunoActivity.class);
            it.putExtra("aluno",alunoAtualizar);
            startActivity(it);



        }


        //PARTE V MENU EXCLUIR
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
            super.onCreateContextMenu(menu, v, menuInfo);
            MenuInflater i = getMenuInflater();
            i.inflate(R.menu.menu_contexto, menu);
        }

        //METODO EXCLUIR
        public void excluir(MenuItem item){
            //pegar qual a posicao do item da lista que eu selecionei para excluir
            AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

            final Aluno alunoExcluir = alunosFiltrados.get(menuInfo.position);
            //mensagem perguntando se quer realmente excluir
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Atenção")
                    .setMessage("Realmente deseja excluir o aluno?")
                    .setNegativeButton("NÃO",null)
                    .setPositiveButton("SIM", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            alunosFiltrados.remove(alunoExcluir);
                            alunos.remove(alunoExcluir);
                            dao.excluir(alunoExcluir);
                            listView.invalidateViews();
                        }
                    } ).create(); //criar a janela
                    dialog.show(); //manda mostrar a janela
        }

        //PARTE IV
        //CONSULTA ALUNO MENU SUPERIOR
        public void procuraAluno(String nome){
            alunosFiltrados.clear();
            for(Aluno a: alunos){
                if(a.getNome().toLowerCase().contains((nome.toLowerCase() ))) {  //converte tudo para minusculo para comparação
                    alunosFiltrados.add(a);
                }
            }
            //invalida os dados da lista antigo para serem atualizados
            listView.invalidateViews();
        }

        //PARTE III
        public void cadastrar(){
            Intent it = new Intent(ListarAlunos.this, CadastroAlunoActivity.class);
            startActivity(it);
        }

        //A hora que volta a tela dos alunos listados para atualizar os novos cadastros
        @Override
        public void onResume(){
        super.onResume(); //método da classe mae
        alunos = dao.obterTodos(); //recebe todos os dados da consulta
        alunosFiltrados.clear();
        alunosFiltrados.addAll(alunos); //adiconar todos os dados da classe aluno
        listView.invalidateViews(); //invalida os dados da lista antiga

        }
}