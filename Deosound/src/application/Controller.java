package application;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Controller {
	@FXML
	private AnchorPane HomePane;
	@FXML
	private Label tempoAtualMusicaDestaque;
	@FXML
	private Slider tempoMusicaDestaque;
	@FXML
	private Label tempoTotalMusicaDestaque;

	@FXML
	private ImageView CapaMusicaDestaque;
	@FXML
	private MediaView MediaView;
	@FXML
	private Label tituloMusicaDestaque;
	@FXML
	private Label CantorMusicaDestaque;
	@FXML
	private Label CantorMusicaDestaquePlayList;
	@FXML
	private Slider volumeAudio;
	@FXML
	private ImageView audio;
	
	@FXML
	private Pane PaneBilioteca;
	@FXML
	private Pane PaneHome;

	private boolean isMaximized = false;
	private MediaPlayer MediaPlayer;
	private List<String> Musicas;
	private int IndiceMusicaAtual;

	public void initialize() {
		CarregarMusic();
		formatarNomeMusica();
		movimentarNomeMUsica();
		definerTempo();
		configurarVolume();

		atualizarCantorMusicaDestaquePlayList();
		definirComportamentoRepeticao();
		PaneHome.setVisible(true);
		PaneBilioteca.setVisible(false);
	}

	public void atualizarCantorMusicaDestaquePlayList() {
		if (CantorMusicaDestaquePlayList != null && tituloMusicaDestaque != null) {
			CantorMusicaDestaquePlayList.setText(tituloMusicaDestaque.getText());
		}
	}

	private void definerTempo() {
		MediaPlayer.setOnReady(() -> {
			Duration TempoTotalMusica = MediaPlayer.getTotalDuration();
			int TotalSegundo = (int) TempoTotalMusica.toSeconds();
			int Minuto = TotalSegundo / 60;
			int segundo = TotalSegundo % 60;
			String TempoFormatado = String.format("%02d:%02d", Minuto, segundo);
			tempoTotalMusicaDestaque.setText(TempoFormatado);
		});

		tempoMusicaDestaque.valueProperty().addListener((obs, valorAntigo, ValorNovo) -> {
			if (tempoMusicaDestaque.isValueChanging()) {
				double posicao = ValorNovo.doubleValue() / 100;
				AlterarTempoMusica(posicao);
			}
		});

		MediaPlayer.currentTimeProperty().addListener((obs, valorAntigo, ValorNovo) -> {
			Duration Tempo = MediaPlayer.getCurrentTime();
			int tempoAtual = (int) Tempo.toSeconds();
			int Minuto = tempoAtual / 60;
			int segundo = tempoAtual % 60;
			String TempoFormatado = String.format("%02d:%02d", Minuto, segundo);
			tempoAtualMusicaDestaque.setText(TempoFormatado);

			if (!tempoMusicaDestaque.isValueChanging()) {
				tempoMusicaDestaque.setValue(ValorNovo.toSeconds() / MediaPlayer.getTotalDuration().toSeconds() * 100);
			}
		});
	}

	private void AlterarTempoMusica(double posicao) {
		Duration duracao = MediaPlayer.getTotalDuration();
		Duration Novaduracao = duracao.multiply(posicao);
		MediaPlayer.seek(Novaduracao);
	}

	private void movimentarNomeMUsica() {
		TranslateTransition transition = new TranslateTransition(Duration.seconds(30), CantorMusicaDestaque);
		transition.setFromX(350);
		transition.setToX(-670);

		transition.setCycleCount(TranslateTransition.INDEFINITE);
		transition.play();
	}

	private void CarregarMusic() {
		// Carregar a lista de músicas e definir a primeira
		Musicas = new ArrayList<>();
		Musicas.add("Music/forca_suprema_snifa_cada_linha_video_oficial_mp3_57439.mp3");
		Musicas.add("Music/rosalia_saoko_official_video_mp3_46832.mp3");

		if (Musicas.isEmpty()) {
			System.out.println("Nenhuma música encontrada.");
			return;
		}

		IndiceMusicaAtual = 0;
		String MusicaAtual = Musicas.get(IndiceMusicaAtual);

		Media media = new Media(new File(MusicaAtual).toURI().toString());
		MediaPlayer = new MediaPlayer(media);
		MediaView.setMediaPlayer(MediaPlayer);

		// Chama o método para formatar e atualizar o cantor
		formatarNomeMusica();
		atualizarCantorMusicaDestaquePlayList(); // Atualiza no início
	}

	private void formatarNomeMusica() {
		String nome = Musicas.get(IndiceMusicaAtual);
		CantorMusicaDestaque.setText(nome.replace("Music/", "").replace(".mp3", ""));
	}

	public void closeApp() {
		System.exit(0);
	}

	public void espandeApp() {
		Stage stage = (Stage) HomePane.getScene().getWindow();

		if (isMaximized) {
			stage.setMaximized(false);
		} else {
			stage.setMaximized(true);
		}

		isMaximized = !isMaximized;
	}

	public void minimizaApp() {
		((Stage) HomePane.getScene().getWindow()).setIconified(true);
	}

	private void AlterarMusicaAtual() {
		String MusicaAtual = Musicas.get(IndiceMusicaAtual);

		Media media = new Media(new File(MusicaAtual).toURI().toString());
		MediaPlayer = new MediaPlayer(media);
		MediaView.setMediaPlayer(MediaPlayer);

		String nomeFormatado = MusicaAtual.replace("Music/", "").replace(".mp3", "");
		CantorMusicaDestaque.setText(nomeFormatado);

		atualizarCantorMusicaDestaquePlayList();

		MediaPlayer.setOnReady(() -> {
			Duration tempoTotal = MediaPlayer.getTotalDuration();
			int totalSegundos = (int) tempoTotal.toSeconds();
			int minutos = totalSegundos / 60;
			int segundos = totalSegundos % 60;
			String tempoFormatadoTotal = String.format("%02d:%02d", minutos, segundos);
			tempoTotalMusicaDestaque.setText(tempoFormatadoTotal);

			tempoMusicaDestaque.setValue(0);
			tempoAtualMusicaDestaque.setText("00:00");
		});

		MediaPlayer.currentTimeProperty().addListener((obs, valorAntigo, valorNovo) -> {
			Duration tempoAtual = MediaPlayer.getCurrentTime();
			int segundosAtual = (int) tempoAtual.toSeconds();
			int minutosAtual = segundosAtual / 60;
			int segundosResto = segundosAtual % 60;
			String tempoFormatadoAtual = String.format("%02d:%02d", minutosAtual, segundosResto);
			tempoAtualMusicaDestaque.setText(tempoFormatadoAtual);

			if (!tempoMusicaDestaque.isValueChanging() && MediaPlayer.getTotalDuration() != null) {
				tempoMusicaDestaque.setValue(tempoAtual.toSeconds() / MediaPlayer.getTotalDuration().toSeconds() * 100);
			}
		});
		MediaPlayer.play();

	}

	public void btnPlay() {
		if (MediaPlayer.getStatus() == javafx.scene.media.MediaPlayer.Status.PLAYING) {
			MediaPlayer.pause();
		} else {
			MediaPlayer.play();
		}
	}

	private boolean shuffleAtivado = false;
    private Random geradorAleatorio = new Random();

    public void AturnOnShuffle() {
        shuffleAtivado = !shuffleAtivado;
        System.out.println("Modo Shuffle: " + (shuffleAtivado ? "Ativado" : "Desativado"));
    }

    public void btnNext() {
        MediaPlayer.stop();
        
        if (shuffleAtivado) {
            IndiceMusicaAtual = geradorAleatorio.nextInt(Musicas.size());
        } else {
            IndiceMusicaAtual++;
            if (IndiceMusicaAtual >= Musicas.size()) {
                IndiceMusicaAtual = 0;
            }
        }

        AlterarMusicaAtual();
        MediaPlayer.play();
    }

    public void btnprev() {
        MediaPlayer.stop();
        
        if (shuffleAtivado) {
            IndiceMusicaAtual = geradorAleatorio.nextInt(Musicas.size());
        } else {
            IndiceMusicaAtual--;
            if (IndiceMusicaAtual < 0) {
                IndiceMusicaAtual = Musicas.size() - 1;
            }
        }

        AlterarMusicaAtual();
        MediaPlayer.play();
    }

	private int estadoRepeticao = 0;

	private void definirComportamentoRepeticao() {
	    MediaPlayer.setOnEndOfMedia(() -> {
	        if (estadoRepeticao == 1) {
	            IndiceMusicaAtual = (IndiceMusicaAtual + 1) % Musicas.size();
	            AlterarMusicaAtual();
	            MediaPlayer.play();
	        } else if (estadoRepeticao == 2) {
	            MediaPlayer.seek(Duration.ZERO);
	            MediaPlayer.play();
	        } else 
	            MediaPlayer.setCycleCount(1);

	    });
	}

	public void repetirMusica() {
	    estadoRepeticao = (estadoRepeticao + 1) % 3;
	}


	public void audioMudo() {
		if (MediaPlayer.isMute())
			MediaPlayer.setMute(false);
		else
			MediaPlayer.setMute(true);
	}

	private void configurarVolume() {
		volumeAudio.setValue(MediaPlayer.getVolume() * 100);
		volumeAudio.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (MediaPlayer != null) {
				MediaPlayer.setVolume(newValue.doubleValue() / 100);
			}
		});
		volumeAudio.setValue(MediaPlayer.getVolume() * 100);
		volumeAudio.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (MediaPlayer != null) {
				MediaPlayer.setVolume(newValue.doubleValue() / 100);
			}
		});
	}

	public void espandePlayList() {
		try {
			// Carrega o FXML da página de playlists
			FXMLLoader loader = new FXMLLoader(getClass().getResource("Playlists.fxml"));

			Parent root = loader.load();

			// Obtém a cena atual e troca o conteúdo da raiz
			HomePane.getScene().setRoot(root);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	public void btn_Bilioteca() {
		PaneBilioteca.setVisible(true);
		PaneHome.setVisible(false);
	}
	@FXML
	public void Pane_Home() {
		PaneHome.setVisible(true);
		PaneBilioteca.setVisible(false);
	}
}
