package com.exemplocrud;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.exemplocrud.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.ByteArrayOutputStream;

public class CadastroAlunoActivity extends AppCompatActivity {

    /*configuracoes para acessar a camera*/
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView imageView;  // Adicione a ImageView para exibir a foto tirada

    //DADOS DE PERMISSAO PARA ACESSAR A CAMERA
    private static final int REQUEST_CAMERA_PERMISSION = 200;

    private ActivityResultLauncher<Intent> cameraLauncher;

    /*configuracoes para acessar a camera*/


    private ActivityMainBinding binding;
    private BottomNavigationView navView;

    private EditText nome;
    private EditText cpf;
    private EditText telefone;

    private AlunoDao dao;

    //PARTE VI
    private Aluno aluno = null;


    private AppBarConfiguration appBarConfiguration;  // Adicionando como variável de instância

    //ONCREATE
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // No CadastroAlunoActivity setar o título
        getSupportActionBar().setTitle(R.string.title_home);


        // CAMERA  - Vincule a ImageView no layout para mostrar a foto
        imageView = findViewById(R.id.imageView);
        // Vincule o botão para tirar foto e configure o listener
        Button btnTakePhoto = findViewById(R.id.btnTakePhoto);
        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tirarFoto();
            }
        });
        //CAMERA


        // Vinculando os campos do layout com as variáveis do Java
        nome = findViewById(R.id.editNome);
        cpf = findViewById(R.id.editCPF);
        telefone = findViewById(R.id.editTelefone);

        dao = new AlunoDao(this);

        //PARTE VI ATUALIZAR
        Intent it = getIntent(); //pega intenção
        if(it.hasExtra("aluno")){
            aluno = (Aluno) it.getSerializableExtra("aluno");
            nome.setText(aluno.getNome().toString());
            cpf.setText(aluno.getCpf());
            telefone.setText(aluno.getTelefone());

            // Carregar a foto no ImageView no momento que carregar os dados para atualizar
            byte[] fotoBytes = aluno.getFotoBytes();
            if (fotoBytes != null && fotoBytes.length > 0) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(fotoBytes, 0, fotoBytes.length);
                imageView.setImageBitmap(bitmap);
            }

        }



        navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        // Encontre o botão de salvar pelo ID
        Button salvarButton = findViewById(R.id.button);

        // Defina um ouvinte de clique para o botão de salvar
        salvarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Chame o método salvar quando o botão for clicado
                salvar();
            }
        });

        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.navigation_home) {
                    // Finalizar todas as atividades existentes
                    //finish();
                    Intent it = new Intent(CadastroAlunoActivity.this, CadastroAlunoActivity.class);
                    startActivity(it);
                    return true;
                } else if (itemId == R.id.navigation_dashboard) {
                    // Lógica para a tela de listar alunos (Dashboard)
                    //finish();
                    startActivity(new Intent(CadastroAlunoActivity.this, ListarAlunos.class));
                    return true;
                } else if (itemId == R.id.navigation_notifications) {
                    // Finalizar todas as atividades existentes
                    return true;
                }
                return false;
            }
        });

        //CAMERA
        cameraLauncher = registerForActivityResult(
                // Registro para obter o resultado da atividade de tirar foto
                new ActivityResultContracts.StartActivityForResult(),

                // Função chamada quando a atividade de tirar foto retorna
                result -> {
                    // Verificar se a operação foi bem-sucedida
                    if (result.getResultCode() == RESULT_OK) {
                        // Obter os dados da intenção de retorno
                        Intent data = result.getData();
                        // Obter os extras da intenção (que contêm a imagem capturada)
                        Bundle extras = data.getExtras();
                        // Obter a imagem capturada como um objeto Bitmap
                        Bitmap imageBitmap = (Bitmap) extras.get("data");
                        // Exibir a imagem na ImageView
                        imageView.setImageBitmap(imageBitmap);
                    }
                }
        );

    }

    // Mantenha o método salvar original
    private void salvar() {
        //Se o aluno não existe e não veio do atualizar, pega e cadastrar ele
        if(aluno==null) {
            aluno = new Aluno();
            aluno.setNome(nome.getText().toString());
            aluno.setCpf(cpf.getText().toString());
            aluno.setTelefone(telefone.getText().toString());

            //FOTO
            BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
            if (drawable != null) {
                Bitmap bitmap = drawable.getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] fotoBytes = stream.toByteArray();
                aluno.setFotoBytes(fotoBytes);
            }
            //FOTO

            // Aqui chamamos o método que valida o CPF localizado em AlunoDao, já está instanciado
            boolean cpfTeste = dao.validaCpf(cpf.getText().toString());
            if(cpfTeste == true){
                long id = dao.inserir(aluno); // Inserir o aluno, retorna '-1' se CPF já existe no BD

                //VERIFICA SE CPF ESTA DUPLICADO
                if(id!=-1){
                    Toast.makeText(this, "Aluno inserido com id: " + id, Toast.LENGTH_SHORT).show();
                }else{
                    // CPF duplicado, trate conforme necessário
                    Toast.makeText(this, "CPF duplicado. Insira um CPF diferente.", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(this, "CPF inválido. Digite novamente.: ", Toast.LENGTH_SHORT).show();
            }

        }
        else{
            //ATUALIZAR
            //seta os valores de novo e atualiza
            aluno.setNome(nome.getText().toString());
            aluno.setCpf(cpf.getText().toString());
            aluno.setTelefone(telefone.getText().toString());

            //VALIDACAO
            // Aqui chamamos o método que valida o CPF localizado em AlunoDao, já está instanciado
            boolean cpfTeste = dao.validaCpf(cpf.getText().toString());
            if(cpfTeste == true){
                dao.atualizar(aluno);
                Toast.makeText(this, "Aluno foi atualizado: ", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "CPF inválido. Digite novamente.: ", Toast.LENGTH_SHORT).show();
            }

        }
    }



    // Verifique e solicite permissões da câmera
    private void startCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            cameraLauncher.launch(takePictureIntent);
        }
    }

    // Verifique e solicite permissões da câmera
    private void checkCameraPermissionAndStart() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Se a permissão não foi concedida, solicite-a
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            // Se a permissão já foi concedida, inicie a câmera
            startCamera();
        }
    }

    // Método chamado quando o botão para tirar foto é clicado
    public void tirarFoto() {
        checkCameraPermissionAndStart();
    }

    // Método chamado após a foto ser tirada
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
        }
    }

    //TRATAMENTO DE PERMISSÕES CONCEDIDAS E NEGADAS DA CAMERA
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            // Verifique se a permissão foi concedida
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permissão concedida, inicie a câmera
                startCamera();
            } else {
                // Permissão negada, informe ao usuário ou tome as medidas apropriadas
                Toast.makeText(this, "A permissão da câmera é necessária para tirar fotos.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    // Este método é necessário para garantir a navegação correta para trás
    /*
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }*/

}