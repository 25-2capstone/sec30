// 전역 뮤직 플레이어
const musicPlayer = {
    audio: null,
    youtubePlayer: null,
    currentTrack: null,
    playlist: [],
    currentIndex: -1,
    isYoutubePlaying: false,
    youtubeReady: false,

    init() {
        console.log('🎵 Music Player Initializing...');
        this.audio = document.getElementById('audio-element');
        this.setupEventListeners();

        // YouTube API가 이미 로드되었는지 확인
        if (window.YT && window.YT.Player) {
            console.log('✅ YouTube API already loaded');
            this.youtubeReady = true;
        }
    },

    setupEventListeners() {
        if (!this.audio) return;

        // 오디오 이벤트 리스너
        this.audio.addEventListener('timeupdate', () => this.updateProgress());
        this.audio.addEventListener('ended', () => this.next());
        this.audio.addEventListener('loadedmetadata', () => this.updateDuration());
        this.audio.addEventListener('play', () => this.updatePlayButton(true));
        this.audio.addEventListener('pause', () => this.updatePlayButton(false));

        // 프로그레스 바 드래그
        const progressBar = document.getElementById('progress-bar');
        if (progressBar) {
            progressBar.addEventListener('input', (e) => {
                const time = (e.target.value / 100) * this.audio.duration;
                document.getElementById('current-time').textContent = this.formatTime(time);
            });
        }
    },

    playTrack(track, playlistTracks = []) {
        console.log('=== musicPlayer.playTrack called ===');
        console.log('Track object:', track);
        console.log('YouTube Video ID:', track.youtubeVideoId);
        console.log('Preview URL:', track.previewUrl);

        this.currentTrack = track;

        if (playlistTracks.length > 0) {
            this.playlist = playlistTracks;
            this.currentIndex = playlistTracks.findIndex(t => t.trackId === track.trackId);
        } else {
            this.playlist = [track];
            this.currentIndex = 0;
        }

        // UI 업데이트
        document.getElementById('player-album-image').src = track.albumImage || track.imageUri || 'https://via.placeholder.com/56';
        document.getElementById('player-track-name').textContent = track.name || track.trackTitle || '-';
        document.getElementById('player-artist-name').textContent = track.artist || track.artistName || '-';

        // 1순위: YouTube 재생 (전체 곡)
        if (track.youtubeVideoId && track.youtubeVideoId !== 'null' && track.youtubeVideoId !== '') {
            console.log('✅ Playing from YouTube:', track.youtubeVideoId);
            this.playYouTube(track.youtubeVideoId);
            return;
        }

        // 2순위: Spotify Preview (30초)
        const hasValidPreviewUrl = track.previewUrl &&
                                   track.previewUrl !== 'null' &&
                                   track.previewUrl !== 'undefined' &&
                                   track.previewUrl !== '' &&
                                   track.previewUrl.startsWith('http');

        if (hasValidPreviewUrl) {
            console.log('✅ Playing Spotify preview:', track.previewUrl);
            this.playAudio(track.previewUrl);
            return;
        }

        // 3순위: Spotify 웹으로 이동
        console.log('❌ No playable source, opening Spotify');
        this.openSpotify();
    },

    playYouTube(videoId) {
        console.log('🎬 Attempting to play YouTube video:', videoId);
        this.isYoutubePlaying = true;

        // 기존 오디오 정지
        if (this.audio && !this.audio.paused) {
            this.audio.pause();
        }

        // YouTube Player 생성 또는 재사용
        if (!this.youtubePlayer) {
            console.log('📺 Creating new YouTube Player...');

            // YouTube API가 로드될 때까지 대기
            const initPlayer = () => {
                if (window.YT && window.YT.Player) {
                    console.log('✅ YouTube API ready, creating player');

                    try {
                        this.youtubePlayer = new YT.Player('youtube-player-container', {
                            height: '1',
                            width: '1',
                            videoId: videoId,
                            playerVars: {
                                autoplay: 1,
                                controls: 0,
                                enablejsapi: 1,
                                origin: window.location.origin
                            },
                            events: {
                                'onReady': (event) => {
                                    console.log('✅ YouTube Player ready, starting playback');
                                    event.target.playVideo();
                                    this.show();
                                    document.body.classList.add('player-active');
                                    this.updatePlayButton(true);
                                },
                                'onStateChange': (event) => {
                                    console.log('YouTube state changed:', event.data);
                                    if (event.data === YT.PlayerState.ENDED) {
                                        this.next();
                                    } else if (event.data === YT.PlayerState.PLAYING) {
                                        this.updatePlayButton(true);
                                        this.startYouTubeProgressUpdate();
                                    } else if (event.data === YT.PlayerState.PAUSED) {
                                        this.updatePlayButton(false);
                                        this.stopYouTubeProgressUpdate();
                                    }
                                },
                                'onError': (event) => {
                                    console.error('❌ YouTube Player error:', event.data);
                                    alert('YouTube 재생 중 오류가 발생했습니다. Spotify로 이동합니다.');
                                    this.openSpotify();
                                }
                            }
                        });
                        this.youtubeReady = true;
                    } catch (err) {
                        console.error('❌ Failed to create YouTube Player:', err);
                        alert('YouTube Player 생성 실패');
                    }
                } else {
                    console.log('⏳ Waiting for YouTube API...');
                    setTimeout(initPlayer, 200);
                }
            };
            initPlayer();
        } else {
            // 기존 플레이어에서 새 비디오 재생
            console.log('♻️ Reusing existing YouTube Player');
            try {
                this.youtubePlayer.loadVideoById(videoId);
                this.show();
                document.body.classList.add('player-active');
                this.updatePlayButton(true);
            } catch (err) {
                console.error('❌ Failed to load video:', err);
                alert('비디오 재생 실패');
            }
        }
    },

    startYouTubeProgressUpdate() {
        if (this.youtubeProgressInterval) {
            clearInterval(this.youtubeProgressInterval);
        }

        this.youtubeProgressInterval = setInterval(() => {
            if (this.youtubePlayer && this.youtubePlayer.getCurrentTime) {
                try {
                    const currentTime = this.youtubePlayer.getCurrentTime();
                    const duration = this.youtubePlayer.getDuration();

                    if (duration > 0) {
                        const progress = (currentTime / duration) * 100;
                        document.getElementById('progress-bar').value = progress;
                        document.getElementById('current-time').textContent = this.formatTime(currentTime);
                        document.getElementById('duration-time').textContent = this.formatTime(duration);
                    }
                } catch (err) {
                    // Player not ready yet
                }
            }
        }, 500);
    },

    stopYouTubeProgressUpdate() {
        if (this.youtubeProgressInterval) {
            clearInterval(this.youtubeProgressInterval);
            this.youtubeProgressInterval = null;
        }
    },

    playAudio(url) {
        this.isYoutubePlaying = false;

        // YouTube Player 정지
        if (this.youtubePlayer && this.youtubePlayer.pauseVideo) {
            this.youtubePlayer.pauseVideo();
        }

        // 오디오 재생
        this.audio.src = url;
        this.audio.play().then(() => {
            console.log('Audio playback started');
            this.show();
            document.body.classList.add('player-active');
        }).catch(err => {
            console.error('Audio playback failed:', err);
            alert('재생 중 오류가 발생했습니다.');
        });
    },

    togglePlay() {
        if (this.isYoutubePlaying && this.youtubePlayer) {
            const state = this.youtubePlayer.getPlayerState();
            if (state === YT.PlayerState.PLAYING) {
                this.youtubePlayer.pauseVideo();
            } else {
                this.youtubePlayer.playVideo();
            }
        } else if (this.audio && this.audio.src) {
            if (this.audio.paused) {
                this.audio.play();
            } else {
                this.audio.pause();
            }
        }
    },

    prev() {
        if (this.playlist.length === 0) return;

        this.currentIndex--;
        if (this.currentIndex < 0) {
            this.currentIndex = this.playlist.length - 1;
        }

        this.playTrack(this.playlist[this.currentIndex], this.playlist);
    },

    next() {
        if (this.playlist.length === 0) return;

        this.currentIndex++;
        if (this.currentIndex >= this.playlist.length) {
            this.currentIndex = 0;
        }

        this.playTrack(this.playlist[this.currentIndex], this.playlist);
    },

    seek(value) {
        if (!this.audio.duration) return;
        const time = (value / 100) * this.audio.duration;
        this.audio.currentTime = time;
    },

    updateProgress() {
        if (!this.audio.duration) return;

        const progress = (this.audio.currentTime / this.audio.duration) * 100;
        document.getElementById('progress-bar').value = progress;
        document.getElementById('current-time').textContent = this.formatTime(this.audio.currentTime);
    },

    updateDuration() {
        if (!this.audio.duration) return;
        document.getElementById('duration-time').textContent = this.formatTime(this.audio.duration);
    },

    updatePlayButton(isPlaying) {
        const playIcon = document.getElementById('play-icon');
        const pauseIcon = document.getElementById('pause-icon');

        if (isPlaying) {
            playIcon.style.display = 'none';
            pauseIcon.style.display = 'block';
        } else {
            playIcon.style.display = 'block';
            pauseIcon.style.display = 'none';
        }
    },

    formatTime(seconds) {
        if (!seconds || isNaN(seconds)) return '0:00';
        const mins = Math.floor(seconds / 60);
        const secs = Math.floor(seconds % 60);
        return `${mins}:${secs.toString().padStart(2, '0')}`;
    },

    openSpotify() {
        if (this.currentTrack && this.currentTrack.spotifyId) {
            window.open('https://open.spotify.com/track/' + this.currentTrack.spotifyId, '_blank');
        } else if (this.currentTrack && this.currentTrack.trackId) {
            window.open('https://open.spotify.com/track/' + this.currentTrack.trackId, '_blank');
        }
    },

    show() {
        document.getElementById('music-player').style.display = 'block';
    },

    close() {
        // 오디오 정지
        if (this.audio) {
            this.audio.pause();
            this.audio.src = '';
        }

        // YouTube Player 정지
        if (this.youtubePlayer && this.youtubePlayer.pauseVideo) {
            this.youtubePlayer.pauseVideo();
        }

        this.stopYouTubeProgressUpdate();

        document.getElementById('music-player').style.display = 'none';
        document.body.classList.remove('player-active');
        this.currentTrack = null;
        this.playlist = [];
        this.currentIndex = -1;
        this.isYoutubePlaying = false;
    }
};

// 페이지 로드 시 초기화
document.addEventListener('DOMContentLoaded', () => {
    musicPlayer.init();
});

// YouTube IFrame API 준비 완료 콜백
window.onYouTubeIframeAPIReady = function() {
    console.log('✅ YouTube IFrame API is ready');
    musicPlayer.youtubeReady = true;
};

