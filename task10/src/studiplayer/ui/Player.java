package studiplayer.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import studiplayer.audio.AudioFile;
import studiplayer.audio.PlayList;
import studiplayer.audio.SampledFile;
import studiplayer.audio.SortCriterion;

import java.io.File;
import java.net.URL;

public class Player extends Application {
	public static final String DEFAULT_PLAYLIST = "playlists/DefaultPlayList.m3u";
	private static final String PLAYLIST_DIRECTORY = "playlists";
	private static final String INITIAL_PLAY_TIME_LABEL = "00:00";
	private static final String NO_CURRENT_SONG = " - ";
	
	
	private Button playButton;
	private Button pauseButton;
	private Button stopButton;
	private Button nextButton;
	private Label playListLabel;
	private Label playTimeLabel;
	private Label currentSongLabel;
	private ChoiceBox<SortCriterion> sortChoiceBox;
	private TextField searchTextField;
	private Button filterButton;
	
	
	private PlayList playList;
	private boolean useCertPlayList = false;
	private SongTable songTable;
	
	
	private PlayerThread playerThread;
	private TimerThread timerThread;
	
	// State tracking
	private boolean isPlaying = false;
	private boolean isPaused = false;
	private boolean isInitialState = true;
	
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
			showPlaylistDialog(stage);
		}
		
		BorderPane root = createFinalLayout();
		
		Scene scene = new Scene(root, 600, 400);
		stage.setScene(scene);
		stage.show();
	}
	
	private void showPlaylistDialog(Stage stage) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select Playlist File");
		fileChooser.setInitialDirectory(new File(PLAYLIST_DIRECTORY));
		fileChooser.getExtensionFilters().add(
						new FileChooser.ExtensionFilter("M3U Files", "*.m3u")
		);
		
		File selectedFile = fileChooser.showOpenDialog(stage);
		if (selectedFile != null) {
			loadPlayList(selectedFile.getAbsolutePath());
		} else {
			loadPlayList(null);
		}
	}
	
	private BorderPane createFinalLayout() {
		BorderPane root = new BorderPane();
		
		TitledPane filterSection = createFilterSection();
		root.setTop(filterSection);
		
		songTable = new SongTable(playList);
		songTable.setRowSelectionHandler(e -> {
			if (e.getClickCount() == 2) {
				Song selectedSong = songTable.getSelectionModel().getSelectedItem();
				if (selectedSong != null) {
					
					if (isPlaying || isPaused) {
						AudioFile currentSong = playList.currentAudioFile();
						if (currentSong != null) {
							currentSong.stop();
						}
						terminateThreads(false);
					}
					
					playList.jumpToAudioFile(selectedSong.getAudioFile());
					isInitialState = false;
					playCurrentSong();
				}
			}
		});
		root.setCenter(songTable);
		
		VBox bottomSection = createBottomSection();
		root.setBottom(bottomSection);
		
		return root;
	}
	
	private TitledPane createFilterSection() {
		TitledPane titledPane = new TitledPane();
		titledPane.setText("Filter");
		titledPane.setCollapsible(true);
		
		GridPane filterGrid = new GridPane();
		filterGrid.setHgap(10);
		filterGrid.setVgap(10);
		filterGrid.setPadding(new Insets(10));
		
		Label searchLabel = new Label("Search:");
		searchTextField = new TextField();
		
		Label sortLabel = new Label("Sort by:");
		sortChoiceBox = new ChoiceBox<>();
		sortChoiceBox.getItems().addAll(SortCriterion.values());
		sortChoiceBox.setValue(SortCriterion.DEFAULT);
		
		filterButton = new Button("Display");
		filterButton.setOnAction(e -> applyFilter());
		
		filterGrid.add(searchLabel, 0, 0);
		filterGrid.add(searchTextField, 1, 0);
		filterGrid.add(sortLabel, 0, 1);
		filterGrid.add(sortChoiceBox, 1, 1);
		filterGrid.add(filterButton, 2, 1);
		
		titledPane.setContent(filterGrid);
		return titledPane;
	}
	
	private VBox createBottomSection() {
		VBox bottomPane = new VBox(10);
		bottomPane.setPadding(new Insets(10));
		
		GridPane infoGrid = createSongInfoGrid();
		HBox controlBox = createControlButtons();
		
		bottomPane.getChildren().addAll(infoGrid, controlBox);
		return bottomPane;
	}
	
	private GridPane createSongInfoGrid() {
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
		
		return infoGrid;
	}
	
	private HBox createControlButtons() {
		HBox controlBox = new HBox(10);
		controlBox.setAlignment(Pos.CENTER);
		
		playButton = createButton("play.jpg");
		pauseButton = createButton("pause.jpg");
		stopButton = createButton("stop.jpg");
		nextButton = createButton("next.jpg");
		
		setButtonStates(false, true, true, false);
		
		playButton.setOnAction(e -> playCurrentSong());
		pauseButton.setOnAction(e -> pauseCurrentSong());
		stopButton.setOnAction(e -> stopCurrentSong());
		nextButton.setOnAction(e -> nextSong());
		
		controlBox.getChildren().addAll(playButton, pauseButton, stopButton, nextButton);
		return controlBox;
	}
	
	private Button createButton(String iconFile) {
		Button button = null;
		try {
			URL url = getClass().getResource("/icons/" + iconFile);
			Image icon = new Image(url.toString());
			ImageView imageView = new ImageView(icon);
			imageView.setFitHeight(20);
			imageView.setFitWidth(20);
			button = new Button("", imageView);
			button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		} catch (Exception e) {
			System.out.println("Image " + "icons/" + iconFile + " not found!");
			System.exit(-1);
		}
		return button;
	}
	
	/*
	private void playCurrentSong() {
		// Don't do anything if already playing (not paused)
		if (isPlaying && !isPaused) {
			return;
		}
		
		AudioFile currentSong = playList.currentAudioFile();
		if (currentSong != null) {
			// If we're resuming from pause, don't stop/restart
			if (!isPaused) {
				currentSong.stop(); // Ensure we start from beginning only if not resuming
			}
			
			updateSongInfo(currentSong);
			setButtonStates(true, false, false, false); // Play disabled, others enabled
			
			System.out.println("Playing " + currentSong.getAuthor() + " - " +
							currentSong.getTitle() + " - " + currentSong +
							" - " + (currentSong instanceof SampledFile ?
							((SampledFile) currentSong).formatDuration() : ""));
			System.out.println("Filename is " + currentSong.getFilename());
			
			isPlaying = true;
			isPaused = false;
			isInitialState = false;
			startThreads(false);
		}
	}
	
	private void pauseCurrentSong() {
		AudioFile currentSong = playList.currentAudioFile();
		if (currentSong != null) {
			if (isPlaying && !isPaused) {
				// Currently playing - pause it
				currentSong.togglePause();
				isPaused = true;
				terminateThreads(true); // Stop only timer thread
				setButtonStates(true, false, false, false); // All buttons enabled except play
				
				System.out.println("Pausing " + currentSong.getAuthor() + " - " +
								currentSong.getTitle() + " - " + currentSong.toString() +
								" - " + (currentSong instanceof SampledFile ?
								((SampledFile) currentSong).formatDuration() : ""));
				System.out.println("Filename is " + currentSong.getFilename());
			} else if (isPaused) {
				// Currently paused - resume playing
				currentSong.togglePause();
				isPaused = false;
				startThreads(true);
				setButtonStates(true, false, false, false); // Back to playing state
			}
		}
	}
	
	private void stopCurrentSong() {
		if (!isPlaying && !isPaused) {
			return;
		}
		
		AudioFile currentSong = playList.currentAudioFile();
		if (currentSong != null) {
			currentSong.stop();
			terminateThreads(false);
			
			System.out.println("Stopping " + currentSong.getAuthor() + " - " +
							currentSong.getTitle() + " - " + currentSong +
							" - " + (currentSong instanceof SampledFile ?
							((SampledFile) currentSong).formatDuration() : ""));
			System.out.println("Filename is " + currentSong.getFilename());
			
			isPlaying = false;
			isPaused = false;
			// Don't reset to initial state - keep current song selected
			
			// Update display with current song but reset play time
			updateSongInfo(currentSong);
			Platform.runLater(() -> {
				playTimeLabel.setText(INITIAL_PLAY_TIME_LABEL);
			});
			setButtonStates(false, true, true, false); // Play and Next enabled, others disabled
		}
	}
	
	private void nextSong() {
		if (isInitialState) {
			// In initial state, just move to next song without playing
			playList.nextSong();
			AudioFile nextSong = playList.currentAudioFile();
			if (nextSong != null) {
				updateSongInfo(nextSong);
				Platform.runLater(() -> {
					playTimeLabel.setText(INITIAL_PLAY_TIME_LABEL);
				});
				setButtonStates(false, true, true, false); // Play and Next enabled
				isInitialState = false; // No longer in initial state
			}
			return;
		}
		
		boolean wasPlaying = isPlaying && !isPaused;
		boolean wasPaused = isPaused;
		AudioFile currentSong = playList.currentAudioFile();
		
		if (currentSong != null && (isPlaying || isPaused)) {
			System.out.println("Switching to next audio file: stopped = " +
							!isPlaying + ", paused = " + isPaused);
			
			// Stop current song
			currentSong.stop();
			terminateThreads(false);
		}
		
		// Move to next song
		playList.nextSong();
		AudioFile nextSong = playList.currentAudioFile();
		
		if (nextSong != null) {
			updateSongInfo(nextSong);
			Platform.runLater(() -> {
				playTimeLabel.setText(INITIAL_PLAY_TIME_LABEL);
			});
			
			if (wasPlaying) {
				// If we were playing, start playing the next song
				isPlaying = true;
				isPaused = false;
				setButtonStates(true, false, false, false); // Playing state
				startThreads(false);
				
				System.out.println("Playing " + nextSong.getAuthor() + " - " +
								nextSong.getTitle() + " - " + nextSong +
								" - " + (nextSong instanceof SampledFile ?
								((SampledFile) nextSong).formatDuration() : ""));
				System.out.println("Filename is " + nextSong.getFilename());
			} else if (wasPaused) {
				// If we were paused, move to next song but stay paused
				isPaused = true;
				isPlaying = false;
				setButtonStates(false, false, false, false); // Paused state
			} else {
				// If we were stopped, just update info
				isPlaying = false;
				isPaused = false;
				setButtonStates(false, true, true, false); // Stopped state
			}
			
			System.out.println("Switched to next audio file: stopped = " +
							!isPlaying + ", paused = " + isPaused);
		}
	}
	*/
	
	private void playCurrentSong() {
		if (isPlaying && !isPaused) {
			return;
		}
		
		AudioFile currentSong = playList.currentAudioFile();
		if (currentSong != null) {
			if (!isPaused) {
				currentSong.stop();
			}
			
			updateSongInfo(currentSong);
			setButtonStates(true, false, false, false);
			
			System.out.println("Playing " + currentSong.getAuthor() + " - " +
							currentSong.getTitle() + " - " + currentSong +
							" - " + (currentSong instanceof SampledFile ?
							((SampledFile) currentSong).formatDuration() : ""));
			System.out.println("Filename is " + currentSong.getFilename());
			
			isPlaying = true;
			isPaused = false;
			isInitialState = false;
			startThreads(false);
		}
	}
	
	private void pauseCurrentSong() {
		AudioFile currentSong = playList.currentAudioFile();
		if (currentSong != null) {
			if (isPlaying && !isPaused) {
				currentSong.togglePause();
				isPaused = true;
				terminateThreads(true);
				setButtonStates(true, false, false, false);
				
				System.out.println("Pausing " + currentSong.getAuthor() + " - " +
								currentSong.getTitle() + " - " + currentSong +
								" - " + (currentSong instanceof SampledFile ?
								((SampledFile) currentSong).formatDuration() : ""));
				System.out.println("Filename is " + currentSong.getFilename());
			} else if (isPaused) {
				currentSong.togglePause();
				isPaused = false;
				startThreads(true); // Start only timer thread when resuming
				// Button states remain the same when resuming
				setButtonStates(true, false, false, false);
			}
		}
	}
	
	private void stopCurrentSong() {
		if (!isPlaying && !isPaused) {
			return;
		}
		
		AudioFile currentSong = playList.currentAudioFile();
		if (currentSong != null) {
			currentSong.stop();
			terminateThreads(false);
			
			System.out.println("Stopping " + currentSong.getAuthor() + " - " +
							currentSong.getTitle() + " - " + currentSong +
							" - " + (currentSong instanceof SampledFile ?
							((SampledFile) currentSong).formatDuration() : ""));
			System.out.println("Filename is " + currentSong.getFilename());
			
			isPlaying = false;
			isPaused = false;
			// Don't reset to initial state - keep current song selected
			
			// Update display with current song but reset play time
			updateSongInfo(currentSong);
			Platform.runLater(() -> {
				playTimeLabel.setText(INITIAL_PLAY_TIME_LABEL);
			});
			setButtonStates(false, true, true, false); // Play and Next enabled, others disabled
		}
	}
	//  1111
	/*private void nextSong() {
		if (isInitialState) {
			// In initial state, just move to next song without playing
			playList.nextSong();
			AudioFile nextSong = playList.currentAudioFile();
			if (nextSong != null) {
				updateSongInfo(nextSong);
				Platform.runLater(() -> {
					playTimeLabel.setText(INITIAL_PLAY_TIME_LABEL);
				});
				setButtonStates(false, true, true, false); // Play and Next enabled
				isInitialState = false; // No longer in initial state
			}
			return;
		}
		
		boolean wasPlaying = isPlaying && !isPaused;
		AudioFile currentSong = playList.currentAudioFile();
		
		if (currentSong != null && (isPlaying || isPaused)) {
			System.out.println("Switching to next audio file: stopped = " +
							!isPlaying + ", paused = " + isPaused);
			
			// Stop current song
			currentSong.stop();
			terminateThreads(false);
		}
		
		// Move to next song
		playList.nextSong();
		AudioFile nextSong = playList.currentAudioFile();
		
		if (nextSong != null) {
			updateSongInfo(nextSong);
			Platform.runLater(() -> {
				playTimeLabel.setText(INITIAL_PLAY_TIME_LABEL);
			});
			
			if (wasPlaying) {
				// If we were playing, start playing the next song
				isPlaying = true;
				isPaused = false;
				setButtonStates(true, false, false, false); // Playing state
				startThreads(false);
				
				System.out.println("Playing " + nextSong.getAuthor() + " - " +
								nextSong.getTitle() + " - " + nextSong +
								" - " + (nextSong instanceof SampledFile ?
								((SampledFile) nextSong).formatDuration() : ""));
				System.out.println("Filename is " + nextSong.getFilename());
			} else {
				// If we were stopped or paused, start playing the next song
				isPlaying = true;
				isPaused = false;
				setButtonStates(true, false, false, false); // Playing state
				startThreads(false);
				
				System.out.println("Playing " + nextSong.getAuthor() + " - " +
								nextSong.getTitle() + " - " + nextSong +
								" - " + (nextSong instanceof SampledFile ?
								((SampledFile) nextSong).formatDuration() : ""));
				System.out.println("Filename is " + nextSong.getFilename());
			}
			
			System.out.println("Switched to next audio file: stopped = " +
							!isPlaying + ", paused = " + isPaused);
		}
	}*/
	
	//  2222
	/*private void nextSong() {
		if (isInitialState) {
			// In initial state, just move to next song without playing
			playList.nextSong();
			AudioFile nextSong = playList.currentAudioFile();
			if (nextSong != null) {
				updateSongInfo(nextSong);
				Platform.runLater(() -> {
					playTimeLabel.setText(INITIAL_PLAY_TIME_LABEL);
				});
				setButtonStates(false, true, true, false); // Play and Next enabled
				isInitialState = false; // No longer in initial state
			}
			return;
		}
		
		boolean wasPlaying = isPlaying && !isPaused;
		boolean wasPaused = isPaused;
		AudioFile currentSong = playList.currentAudioFile();
		
		if (currentSong != null && (isPlaying || isPaused)) {
			System.out.println("Switching to next audio file: stopped = " +
							!isPlaying + ", paused = " + isPaused);
			
			// Stop current song
			currentSong.stop();
			terminateThreads(false);
		}
		
		// Move to next song
		playList.nextSong();
		AudioFile nextSong = playList.currentAudioFile();
		
		if (nextSong != null) {
			updateSongInfo(nextSong);
			Platform.runLater(() -> {
				playTimeLabel.setText(INITIAL_PLAY_TIME_LABEL);
			});
			
			// Always start playing the next song when pressing next
			// (regardless of previous state - this matches test expectations)
			isPlaying = true;
			isPaused = false;
			setButtonStates(true, false, false, false); // Playing state
			startThreads(false);
			
			System.out.println("Playing " + nextSong.getAuthor() + " - " +
							nextSong.getTitle() + " - " + nextSong +
							" - " + (nextSong instanceof SampledFile ?
							((SampledFile) nextSong).formatDuration() : ""));
			System.out.println("Filename is " + nextSong.getFilename());
			
			System.out.println("Switched to next audio file: stopped = " +
							!isPlaying + ", paused = " + isPaused);
		}
	}*/
	
	//  3333
	private void nextSong() {
		if (isInitialState) {
			// In initial state, just move to next song without playing
			playList.nextSong();
			AudioFile nextSong = playList.currentAudioFile();
			if (nextSong != null) {
				updateSongInfo(nextSong);
				Platform.runLater(() -> {
					playTimeLabel.setText(INITIAL_PLAY_TIME_LABEL);
				});
				setButtonStates(false, true, true, false); // Play and Next enabled
				isInitialState = false; // No longer in initial state
			}
			return;
		}
		
		AudioFile currentSong = playList.currentAudioFile();
		
		if (currentSong != null && (isPlaying || isPaused)) {
			System.out.println("Switching to next audio file: stopped = " +
							!isPlaying + ", paused = " + isPaused);
			
			// Stop current song
			currentSong.stop();
			terminateThreads(false);
		}
		
		// Move to next song
		playList.nextSong();
		AudioFile nextSong = playList.currentAudioFile();
		
		if (nextSong != null) {
			// Always start playing the next song when pressing next
			// (regardless of previous state - this matches test expectations)
			isPlaying = true;
			isPaused = false;
			setButtonStates(true, false, false, false); // Playing state
			
			updateSongInfo(nextSong);
			Platform.runLater(() -> {
				playTimeLabel.setText(INITIAL_PLAY_TIME_LABEL);
			});
			
			// Start threads to begin playback
			startThreads(false);
			
			System.out.println("Playing " + nextSong.getAuthor() + " - " +
							nextSong.getTitle() + " - " + nextSong +
							" - " + (nextSong instanceof SampledFile ?
							((SampledFile) nextSong).formatDuration() : ""));
			System.out.println("Filename is " + nextSong.getFilename());
			
			System.out.println("Switched to next audio file: stopped = " +
							!isPlaying + ", paused = " + isPaused);
		}
	}
	
	private void applyFilter() {
		String searchText = searchTextField.getText();
		SortCriterion selectedSort = sortChoiceBox.getValue();
		
		playList.setSearch(searchText);
		playList.setSortCriterion(selectedSort);
		songTable.refreshSongs();
	}
	
	private void setButtonStates(boolean playDisabled, boolean pauseDisabled,
	                             boolean stopDisabled, boolean nextDisabled) {
		Platform.runLater(() -> {
			playButton.setDisable(playDisabled);
			pauseButton.setDisable(pauseDisabled);
			stopButton.setDisable(stopDisabled);
			nextButton.setDisable(nextDisabled);
		});
	}
	
	private void updateSongInfo(AudioFile af) {
		Platform.runLater(() -> {
			if (af == null || isInitialState) {
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
	
	private void startThreads(boolean onlyTimer) {
		if (timerThread == null) {
			timerThread = new TimerThread();
			timerThread.start();
		}
		
		if (!onlyTimer && playerThread == null) {
			playerThread = new PlayerThread();
			playerThread.start();
		}
	}
	
	private void terminateThreads(boolean onlyTimer) {
		if (timerThread != null) {
			timerThread.terminate();
			timerThread = null;
		}
		
		if (!onlyTimer && playerThread != null) {
			playerThread.terminate();
			playerThread = null;
		}
	}
	
	public void loadPlayList(String pathname) {
		if (pathname == null || pathname.isEmpty()) {
			playList = new PlayList(DEFAULT_PLAYLIST);
		} else {
			playList = new PlayList(pathname);
		}
		
		// Reset to initial state when loading new playlist
		isInitialState = true;
		isPlaying = false;
		isPaused = false;
		
		if (playListLabel != null) {
			playListLabel.setText(pathname != null ? pathname : DEFAULT_PLAYLIST);
		}
	}
	
	public void setUseCertPlayList(boolean value) {
		this.useCertPlayList = value;
	}
	
	/*private class PlayerThread extends Thread {
		private boolean stopped = false;
		
		public void terminate() {
			stopped = true;
		}
		
		@Override
		public void run() {
			while (!stopped) {
				AudioFile currentSong = playList.currentAudioFile();
				if (currentSong != null && !stopped && isPlaying) {
					try {
						Platform.runLater(() -> songTable.selectSong(currentSong));
						
						currentSong.play();
						
						while (!stopped && isPlaying && !isPaused) {
							Thread.sleep(100);
						}
						
						if (!stopped && isPlaying && !isPaused) {
							Platform.runLater(() -> {
								playList.nextSong();
								AudioFile nextSong = playList.currentAudioFile();
								if (nextSong != null) {
									updateSongInfo(nextSong);
								} else {
									stopCurrentSong();
								}
							});
						}
					} catch (Exception e) {
						Platform.runLater(() -> stopCurrentSong());
						break;
					}
				} else {
					break;
				}
			}
		}
	}*/
	
	private class PlayerThread extends Thread {
		private boolean stopped = false;
		public void terminate() {
			stopped = true;
		}
		
		@Override
		public void run() {
			while (!stopped) {
				AudioFile currentSong = playList.currentAudioFile();
				if (currentSong != null && !stopped && isPlaying) {
					try {
						Platform.runLater(() -> songTable.selectSong(currentSong));
						
						currentSong.play();
						
						while (!stopped && isPlaying && !isPaused) {
							Thread.sleep(100);
						}
						
						// Check if we exited because the song finished naturally
						// (not because of stop/pause)
						if (!stopped && isPlaying && !isPaused) {
							Platform.runLater(() -> {
								playList.nextSong();
								AudioFile nextSong = playList.currentAudioFile();
								if (nextSong != null) {
									updateSongInfo(nextSong);
								} else {
									stopCurrentSong();
								}
							});
						}
					} catch (Exception e) {
						Platform.runLater(Player.this::stopCurrentSong);
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
						updateSongInfo(currentSong);
					}
				} catch (InterruptedException e) {
					break;
				}
			}
		}
	}
}