package studiplayer.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.geometry.Pos;
import javafx.geometry.HPos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.scene.image.Image;
import studiplayer.audio.AudioFile;
import studiplayer.audio.SampledFile;
import studiplayer.audio.SortCriterion;
import studiplayer.audio.PlayList;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;

public class Player extends Application {
	private PlayList playList;
	private boolean useCertPlayList = false;
	public static final String DEFAULT_PLAYLIST = "playlists/DefaultPlayList.m3u";
	private static final String PLAYLIST_DIRECTORY = "playlists";
	private static final String INITIAL_PLAY_TIME_LABEL = "00:00";
	private static final String NO_CURRENT_SONG = " - ";
	private SongTable songTable;
	private ChoiceBox<SortCriterion> sortChoiceBox;
	private TextField searchTextField;
	private Button filterButton;
	
	private Button playButton;
	private Button pauseButton;
	private Button stopButton;
	private Button nextButton;
	
	private Label currentSongLabel;
	private Label playListLabel;
	private Label playTimeLabel;
	
	
	private TimerThread timerThread;
	private PlayerThread playerThread;
	
	private boolean playing = false;
	private boolean paused = false;
	private boolean justStarted = true;
	
	public Player() {
	}
	
	public static void main(String[] args) {
		Application.launch(args);
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		stage.setTitle("APA Player");
		
		if (useCertPlayList) {
			loadPlayList("playlists/playList.cert.m3u");
		} else {
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
			alert.setTitle("Playlist Selection");
			alert.setHeaderText("Choose how to load your playlist:");
			alert.setContentText("Select an option:");
			
			ButtonType chooseFileButton = new ButtonType("Choose Playlist File");
			ButtonType defaultButton = new ButtonType("Go with the Default");
			ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
			
			alert.getButtonTypes().setAll(chooseFileButton, defaultButton, cancelButton);
			
			alert.showAndWait().ifPresent(buttonType -> {
				if (buttonType == chooseFileButton) {
					FileChooser fileChooser = new FileChooser();
					fileChooser.setTitle("Select Playlist File");
					fileChooser.setInitialDirectory(new File(PLAYLIST_DIRECTORY));
					
					File selectedFile = fileChooser.showOpenDialog(stage);
					if (selectedFile != null) {
						loadPlayList(selectedFile.getAbsolutePath());
					} else {
						loadPlayList(null);
					}
				} else if (buttonType == defaultButton) {
					loadPlayList(null);
				} else {
					loadPlayList(null);
				}
			});
		}
		
		BorderPane mainPane = new BorderPane();
		
		TitledPane filterPane = new TitledPane();
		filterPane.setText("Filter");
		filterPane.setCollapsible(true);
		
		GridPane filterGrid = new GridPane();
		filterGrid.setHgap(10);
		filterGrid.setVgap(10);
		filterGrid.setPadding(new Insets(10));
		
		Label searchLabel = new Label("Search:");
		searchTextField = new TextField();
		
		
		Label sortLabel = new Label("Sort by:");
		sortChoiceBox = new ChoiceBox<>();
		for (SortCriterion criterion : SortCriterion.values()) {
			sortChoiceBox.getItems().add(criterion);
		}
		sortChoiceBox.setValue(SortCriterion.DEFAULT);
		filterButton = new Button("Display");
		filterButton.setOnAction(e -> {
			
			String searchText = searchTextField.getText();
			SortCriterion selectedSort = sortChoiceBox.getValue();
			
			playList.setSearch(searchText);
			playList.setSortCriterion(selectedSort);
			songTable.refreshSongs();
		});
		
		filterGrid.add(searchLabel, 0, 0);
		filterGrid.add(searchTextField, 1, 0);
		filterGrid.add(sortLabel, 0, 1);
		filterGrid.add(sortChoiceBox, 1, 1);
		filterGrid.add(filterButton, 2, 1);
		
		filterPane.setContent(filterGrid);
		mainPane.setTop(filterPane);
		
		songTable = new SongTable(playList);
		songTable.setRowSelectionHandler(e -> {
			if (e.getClickCount() == 2) {
				Song selectedSong = songTable.getSelectionModel().getSelectedItem();
				if (selectedSong != null) {
					if (playing || paused) {
						AudioFile currentSong = playList.currentAudioFile();
						if (currentSong != null) {
							currentSong.stop();
						}
						stopThreads();
					}
					
					playList.jumpToAudioFile(selectedSong.getAudioFile());
					justStarted = false;
					handlePlayButton();
				}
			}
		});
		mainPane.setCenter(songTable);
		
		VBox bottomBox = new VBox(10);
		bottomBox.setPadding(new Insets(10));
		
		GridPane infoGrid = new GridPane();
		infoGrid.setHgap(10);
		infoGrid.setVgap(5);
		
		Label playlistLbl = new Label("Playlist:");
		Label currentSongLbl = new Label("Current Song:");
		Label playTimeLbl = new Label("Play Time:");
		
		playListLabel = new Label(playList != null ? DEFAULT_PLAYLIST : "No playlist");
		currentSongLabel = new Label(NO_CURRENT_SONG);
		playTimeLabel = new Label(INITIAL_PLAY_TIME_LABEL);
		
		infoGrid.add(playlistLbl, 0, 0);
		infoGrid.add(playListLabel, 1, 0);
		infoGrid.add(currentSongLbl, 0, 1);
		infoGrid.add(currentSongLabel, 1, 1);
		infoGrid.add(playTimeLbl, 0, 2);
		infoGrid.add(playTimeLabel, 1, 2);
		
		HBox buttonBox = new HBox(10);
		buttonBox.setAlignment(Pos.CENTER);
		
		playButton = createButton("play.jpg");
		pauseButton = createButton("pause.jpg");
		stopButton = createButton("stop.jpg");
		nextButton = createButton("next.jpg");
		
		updateButtonStates(false, true, true, false);
		
		playButton.setOnAction(e -> handlePlayButton());
		pauseButton.setOnAction(e -> handlePauseButton());
		stopButton.setOnAction(e -> handleStopButton());
		nextButton.setOnAction(e -> handleNextButton());
		
		buttonBox.getChildren().addAll(playButton, pauseButton, stopButton, nextButton);
		
		bottomBox.getChildren().addAll(infoGrid, buttonBox);
		mainPane.setBottom(bottomBox);
		
		Scene scene = new Scene(mainPane, 600, 400);
		stage.setScene(scene);
		stage.show();
	}
	
	
	
	private Button createButton(String iconfile) {
		Button button = null;
		try {
			URL url = getClass().getResource("/icons/" + iconfile);
			Image icon = new Image(url.toString());
			ImageView imageView = new ImageView(icon);
			imageView.setFitHeight(20);
			imageView.setFitWidth(20);
			button = new Button("", imageView);
			button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
			button.setStyle("-fx-background-color: #fff;");
		} catch (Exception e) {
			System.out.println("Image " + "icons/" + iconfile + " not found!");
			System.exit(-1);
		}
		return button;
	}
	
	private void handlePlayButton() {
		if (playing && !paused) {
			return;
		}
		
		AudioFile currentSong = playList.currentAudioFile();
		if (currentSong != null) {
			if (!paused) {
				currentSong.stop();
			}
			
			updateCurrentSongDisplay(currentSong);
			updateButtonStates(true, false, false, false);
			
			System.out.println("Playing " + currentSong.getAuthor() + " - " +
							currentSong.getTitle() + " - " + currentSong +
							" - " + (currentSong instanceof SampledFile ?
							((SampledFile) currentSong).formatDuration() : ""));
			System.out.println("Filename is " + currentSong.getFilename());
			
			playing = true;
			paused = false;
			justStarted = false;
			
			startPlaybackThreads(false);
		}
	}
	
	private void handlePauseButton() {
		AudioFile currentSong = playList.currentAudioFile();
		if (currentSong != null) {
			if (playing && !paused) {
				currentSong.togglePause();
				paused = true;
				stopTimerThread();
				updateButtonStates(true, false, false, false);
				
				System.out.println("Pausing " + currentSong.getAuthor() + " - " +
								currentSong.getTitle() + " - " + currentSong +
								" - " + (currentSong instanceof SampledFile ?
								((SampledFile) currentSong).formatDuration() : ""));
				System.out.println("Filename is " + currentSong.getFilename());
			} else if (paused) {
				currentSong.togglePause();
				paused = false;
				startTimerThread();
				updateButtonStates(true, false, false, false);
			}
		}
	}
	
	private void handleStopButton() {
		if (!playing && !paused) {
			return;
		}
		
		AudioFile currentSong = playList.currentAudioFile();
		if (currentSong != null) {
			currentSong.stop();
			stopThreads();
			
			System.out.println("Stopping " + currentSong.getAuthor() + " - " +
							currentSong.getTitle() + " - " + currentSong +
							" - " + (currentSong instanceof SampledFile ?
							((SampledFile) currentSong).formatDuration() : ""));
			System.out.println("Filename is " + currentSong.getFilename());
			
			playing = false;
			paused = false;
			
			updateCurrentSongDisplay(currentSong);
			Platform.runLater(() -> {
				playTimeLabel.setText(INITIAL_PLAY_TIME_LABEL);
			});
			updateButtonStates(false, true, true, false);
		}
	}
	
	private void handleNextButton() {
		if (justStarted) {
			playList.nextSong();
			AudioFile nextSong = playList.currentAudioFile();
			if (nextSong != null) {
				updateCurrentSongDisplay(nextSong);
				Platform.runLater(() -> {
					playTimeLabel.setText(INITIAL_PLAY_TIME_LABEL);
				});
				updateButtonStates(false, true, true, false);
				justStarted = false;
			}
			return;
		}
		
		AudioFile currentSong = playList.currentAudioFile();
		
		if (currentSong != null && (playing || paused)) {
			System.out.println("Switching to next audio file: stopped = " +
							!playing + ", paused = " + paused);
			
			currentSong.stop();
			stopThreads();
		}
		
		playList.nextSong();
		AudioFile nextSong = playList.currentAudioFile();
		
		if (nextSong != null) {
			playing = true;
			paused = false;
			updateButtonStates(true, false, false, false);
			
			updateCurrentSongDisplay(nextSong);
			Platform.runLater(() -> {
				playTimeLabel.setText(INITIAL_PLAY_TIME_LABEL);
			});
			
			
			startPlaybackThreads(false);
			
			System.out.println("Playing " + nextSong.getAuthor() + " - " +
							nextSong.getTitle() + " - " + nextSong +
							" - " + (nextSong instanceof SampledFile ?
							((SampledFile) nextSong).formatDuration() : ""));
			System.out.println("Filename is " + nextSong.getFilename());
			
			System.out.println("Switched to next audio file: stopped = " +
							!playing + ", paused = " + paused);
		}
	}
	
	private void updateButtonStates(boolean playDisabled, boolean pauseDisabled,
	                                boolean stopDisabled, boolean nextDisabled) {
		Platform.runLater(() -> {
			playButton.setDisable(playDisabled);
			pauseButton.setDisable(pauseDisabled);
			stopButton.setDisable(stopDisabled);
			nextButton.setDisable(nextDisabled);
		});
	}
	
	
	private void updateCurrentSongDisplay(AudioFile af) {
		Platform.runLater(() -> {
			if (af == null || justStarted) {
				currentSongLabel.setText(NO_CURRENT_SONG);
				playTimeLabel.setText(INITIAL_PLAY_TIME_LABEL);
			} else {
				String songInfo = af.getAuthor() + " - " + af.getTitle();
				currentSongLabel.setText(songInfo);
				
				if (af instanceof SampledFile) {
					SampledFile sf = (SampledFile) af;
					playTimeLabel.setText(sf.formatPosition());
				} else {
					playTimeLabel.setText(INITIAL_PLAY_TIME_LABEL);
				}
			}
		});
	}
	
	private void startPlaybackThreads(boolean timer) {
		if (timerThread == null) {
			timerThread = new TimerThread();
			timerThread.start();
		}
		
		if (!timer && playerThread == null) {
			playerThread = new PlayerThread();
			playerThread.start();
		}
	}
	
	private void stopThreads() {
		if (timerThread != null) {
			timerThread.terminate();
			timerThread = null;
		}
		
		if (playerThread != null) {
			playerThread.terminate();
			playerThread = null;
		}
	}
	
	
	private void stopTimerThread() {
		if (timerThread != null) {
			timerThread.terminate();
			timerThread = null;
		}
	}
	
	
	private void startTimerThread() {
		if (timerThread == null) {
			timerThread = new TimerThread();
			timerThread.start();
		}
	}
	
	public void loadPlayList(String pathname) {
		if (pathname == null || pathname.isEmpty()) {
			playList = new PlayList(DEFAULT_PLAYLIST);
		} else {
			playList = new PlayList(pathname);
		}
		
		justStarted = true;
		playing = false;
		paused = false;
		
		if (playListLabel != null) {
			playListLabel.setText(pathname != null ? pathname : DEFAULT_PLAYLIST);
		}
	}
	
	public void setUseCertPlayList(boolean value) {
		this.useCertPlayList = value;
	}
	
	private class PlayerThread extends Thread {
		private boolean stopped = false;
		
		public void terminate() {
			stopped = true;
		}
		
		@Override
		public void run() {
			while (!stopped) {
				AudioFile currentSong = playList.currentAudioFile();
				if (currentSong != null && !stopped && playing) {
					try {
						Platform.runLater(() -> songTable.selectSong(currentSong));
						
						currentSong.play();
						
						while (!stopped && playing && !paused) {
							Thread.sleep(100);
						}
						
						if (!stopped && playing && !paused) {
							Platform.runLater(() -> {
								playList.nextSong();
								AudioFile nextSong = playList.currentAudioFile();
								if (nextSong != null) {
									updateCurrentSongDisplay(nextSong);
								} else {
									handleStopButton();
								}
							});
						}
					} catch (Exception e) {
						Platform.runLater(Player.this::handleStopButton);
						break;
					}
				} else {
					break;
				}
			}
		}
	}
	
	private class TimerThread extends Thread {
		private boolean stopped = false;
		
		public void terminate() {
			stopped = true;
		}
		
		@Override
		public void run() {
			while (!stopped) {
				try {
					Thread.sleep(100);
					
					if (!stopped) {
						AudioFile currentSong = playList.currentAudioFile();
						updateCurrentSongDisplay(currentSong);
					}
				} catch (InterruptedException e) {
					break;
				}
			}
		}
	}
}