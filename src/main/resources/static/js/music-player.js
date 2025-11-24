// 전역 뮤직 플레이어
const musicPlayer = {
    audio: null,
    currentTrack: null,
    playlist: [],
    currentIndex: -1,

    init() {
        this.audio = document.getElementById('audio-element');
        this.setupEventListeners();
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
        console.log('Track keys:', Object.keys(track));
        console.log('Preview URL:', track.previewUrl);
        console.log('Preview URL type:', typeof track.previewUrl);

        this.currentTrack = track;

        if (playlistTracks.length > 0) {
            this.playlist = playlistTracks;
            this.currentIndex = playlistTracks.findIndex(t => t.trackId === track.trackId);
        } else {
            this.playlist = [track];
            this.currentIndex = 0;
        }

        // 미리듣기 URL이 없으면 Spotify로
        if (!track.previewUrl || track.previewUrl === 'null' || track.previewUrl === '' || track.previewUrl === null) {
            console.log('❌ No preview URL available, opening Spotify');
            console.log('Preview URL value:', track.previewUrl);
            this.openSpotify();
            return;
        }

        console.log('✅ Preview URL found:', track.previewUrl);

        // UI 업데이트
        document.getElementById('player-album-image').src = track.albumImage || track.imageUri || 'https://via.placeholder.com/56';
        document.getElementById('player-track-name').textContent = track.name || track.trackTitle || '-';
        document.getElementById('player-artist-name').textContent = track.artist || track.artistName || '-';

        // 오디오 재생
        this.audio.src = track.previewUrl;
        this.audio.play().then(() => {
            console.log('Playback started');
            this.show();
            document.body.classList.add('player-active');
        }).catch(err => {
            console.error('Playback failed:', err);
            alert('재생 중 오류가 발생했습니다.');
        });
    },

    togglePlay() {
        if (!this.audio.src) return;

        if (this.audio.paused) {
            this.audio.play();
        } else {
            this.audio.pause();
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
        this.audio.pause();
        this.audio.src = '';
        document.getElementById('music-player').style.display = 'none';
        document.body.classList.remove('player-active');
        this.currentTrack = null;
        this.playlist = [];
        this.currentIndex = -1;
    }
};

// 페이지 로드 시 초기화
document.addEventListener('DOMContentLoaded', () => {
    musicPlayer.init();
});

