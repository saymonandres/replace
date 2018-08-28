/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package replace;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author SAYMONLA
 */
public class Replace {
    private String nomeOld;
    private String nomeNew;
    private String nomeOldFirstUpper;
    private String nomeOldUpper;
    private String nomeNewFirstUpper;
    private String nomeNewUpper;
    
    //construtor
    public Replace (String nomeOld, String nomeNew){
        //nome antigo
        this.nomeOld = nomeOld;
        //nome antigo - tratamento primeira letra maiuscula
        this.nomeOldFirstUpper = nomeOld.substring(0, 1).toUpperCase();
        String tempOld = nomeOld.substring(1);
        this.nomeOldFirstUpper = this.nomeOldFirstUpper.concat(tempOld);
        //nome antigo - tratamento todas letras maiusculas
        this.nomeOldUpper = nomeOld.toUpperCase();
        
        //novo nome
        this.nomeNew = nomeNew;
        //novo nome - tratamento primeira letra maiuscula
        this.nomeNewFirstUpper = nomeNew.substring(0, 1).toUpperCase();
        String tempNew = nomeNew.substring(1);
        this.nomeNewFirstUpper = this.nomeNewFirstUpper.concat(tempNew);
        //novo nome - tratamento todas letras maiusculas
        this.nomeNewUpper = nomeNew.toUpperCase();

    }
    
    //metodo que executa a leitura em cada arquivo e faz a substituição
    public void execute (File folder){
        //lista arquivos da pasta
        File[] listOfFiles = folder.listFiles();
        
        //percorre cada arquivo da pasta
        for (int i = 0; i < listOfFiles.length; i++) {

            //verifica se arquivo é pasta    
            if (listOfFiles[i].isDirectory()){
                //se for pasta chama metodo novamente
                String dir = listOfFiles[i].getAbsolutePath();
                System.out.println("Directory: " + listOfFiles[i].getName());
                execute(new File(dir));
            }
            
            //verifica se é arquivo
            else if (listOfFiles[i].isFile()) {
                
                try{
                    //duplica arquivo
                    File origem = listOfFiles[i];
                    FileInputStream fis = new FileInputStream(origem);
                    File destino = new File(listOfFiles[i].getName()+"1");
                    FileOutputStream fos = new FileOutputStream(destino);
                    int count = 0;
                    byte[] bytes = new byte[1024];
                    while((count = fis.read(bytes))>=0)
                            fos.write(bytes,0,count);
                    
                    //passa arquivo temporariamente duplicado para leitura e arquivo original para escrita
                    try (BufferedReader inputStream = new BufferedReader(new FileReader(destino));
                        PrintWriter outputStream = new PrintWriter(new FileWriter(listOfFiles[i].getAbsolutePath()))) {

                        String line;
                        while ((line = inputStream.readLine()) != null) {
                            //verifica em cada linha se existe a palavra antiga, tratando maiusculas
                            String replace = line.replace(nomeOld, nomeNew);
                            replace = replace.replace(nomeOldFirstUpper, nomeNewFirstUpper);
                            replace = replace.replace(nomeOldUpper, nomeNewUpper);
                            //escreve no arquivo a linha
                            outputStream.println(replace);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    System.out.println("File: " + listOfFiles[i].getName());

                } catch (IOException e){
                    e.printStackTrace();
                }

            }
            else {
                System.out.println("Não é arquivo nem pasta");
            }
        }
        
    }
    
    //metodo que verifica se os titulos dos arquivos possuem a palavra e substitui pela nova caso necessario 
    public void executeNameFile (File folder){
        //lista de arquivos da pasta
        File[] listOfFiles = folder.listFiles();

        //percorre cada arquivo da pasta
        for (int i = 0; i < listOfFiles.length; i++) {
            
            //verifica se é pasta e chama o metodo de novo
            if (listOfFiles[i].isDirectory()){
                String dir = listOfFiles[i].getAbsolutePath();
                executeNameFile(new File(dir));
            }
                                    
            try {
                String tempNome = listOfFiles[i].getName();
                //verifica se titulo contem a palavra
                if (tempNome.contains(nomeOld)){
                    //faz a substituição se houver
                    String replaceNomeArq = tempNome.replace(nomeOld, nomeNew);
                    Path source = Paths.get(listOfFiles[i].getAbsolutePath());
                    Files.move(source, source.resolveSibling(replaceNomeArq));
                }
            } 
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        
    }

    
    public static void main(String[] args) {
        
        //recebe a palavra antiga e a nova
        String oldName = JOptionPane.showInputDialog("Digite o nome antigo do tema");
        String newName = JOptionPane.showInputDialog("Digite o novo nome do tema");
	System.out.println("Nome antigo é: "+oldName);
        System.out.println("Novo nome é: "+newName);
        
        //cria classe
        Replace r = new Replace(oldName, newName);
        
        //escolhe pasta
        JFileChooser fileChooser = new JFileChooser();
        int retorno = fileChooser.showOpenDialog(null);

        if (retorno == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getCurrentDirectory();
            
            //pega o caminho escolhido e executa os dois metodos
            r.executeNameFile(file);
            r.execute(file);
        } else {
            System.out.println("Erro ao selecionar arquivo");
        }
    }
    
}
