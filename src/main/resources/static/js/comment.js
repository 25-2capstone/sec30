const token = localStorage.getItem('access_token');

/*
* 설계 목적 :
* 현재 HTML에서 모델 방식으로 하나하나 동기적 방식으로 플레이리스트 정보 및 댓글을 가져옵니다.
* 하지만 모든 일이 동기적으로 일어날 시에 너무나 불편하기에, API 응답이라는 동기적인 작업 이후,
* 댓글 목록을 서버에서 받아온 HTML 프래그먼트로 교체해서 비동기성을 추가한 함수입니다.
*
* 작동원리 :
* AJAX로 서버 댓글 프래그먼트에 GET 요청을 보냅니다.
* 여기서 응답받은 HTML을 #comments-container 요소로 교체합니다.
* 이러면 DOM 요소가 추가돼도 이벤트 위임으로 자동 감지가 되어 비동기적인 기능을 구현할 수 있습니다.
*
* int playlistId - 플레이리스트 id입니다.
*
* */

function refreshComments(playlistId) {

    $.ajax({
        url: `/api/playlists/${playlistId}/comments/list`,
        type: 'GET',
        dataType: 'html',
        success: function(result) {
            console.log('프래그먼트 작동 완료');
            $('#comments-container').replaceWith(result);
        },
        error: function(xhr, status, error) {
            console.error('새로고침 실패:', error);
            alert('댓글 목록 불러오기 실패');
        }
    });
}

/*
* 작동 원리:
* 댓글 작성 후, AJAX로 서버에 POST 요청을 보냅니다.
* 성공 시 refreshComments() 함수가 로드 됩니다.
* */

function initializeCommentListeners() {

    const commentCreateButton = document.getElementById('comment-create-btn');

    if (!commentCreateButton) {
        console.error('작성 버튼 찾지 못함');
        return;
    }

    console.log('작성 버튼 정상 작동');

    // 댓글 작성 버튼
    commentCreateButton.addEventListener('click', function(event) {
        event.preventDefault();

        const playlistId = document.getElementById('playlist-id')?.value;
        const commentContent = document.getElementById('comment-content')?.value;

        if (!commentContent || commentContent.trim() === "") {
            alert("공백 또는 입력하지 않은 부분이 있습니다.");
            return;
        }

        $.ajax({
            url: `/api/playlists/${playlistId}/comments`,
            type: 'POST',
            contentType: 'application/json;',
            dataType: 'json',
            data: JSON.stringify({
                comment: commentContent
            }),
            beforeSend: function(xhr) {
                if (token) {
                    xhr.setRequestHeader('Authorization', 'Bearer ' + token);
                }
            },
            success: function(response) {
                console.log('댓글 작성 완료:', response);
                alert('등록 완료되었습니다.');
                document.getElementById('comment-content').value = '';
                refreshComments(playlistId);
            },
            error: function(xhr) {
                console.error('댓글 작성 실패:', xhr.responseText);
                alert('등록 실패했습니다.');
            }
        });
    });
}


/*
* 작동 원리:
* 댓글 삭제 후, AJAX로 서버에 POST 요청을 보냅니다.
* 성공 시 refreshComments() 함수가 로드 됩니다.
* */

document.addEventListener('click', function(e) {
    const playlistId = document.getElementById('playlist-id')?.value;

    // 삭제 버튼
    if (e.target.classList.contains('comment-delete-btn')) {
        console.log('삭제 버튼 작동');
        e.stopPropagation();

        const commentId = e.target.getAttribute('data-reply-id');

        if (confirm('선택하신 댓글을 삭제할까요?')) {
            $.ajax({
                url: `/api/playlists/${playlistId}/comments/${commentId}`,
                type: 'DELETE',
                beforeSend: function(xhr) {
                    if (token) {
                        xhr.setRequestHeader('Authorization', 'Bearer ' + token);
                    }
                },
                success: function() {
                    console.log('댓글 삭제됨');
                    alert('삭제되었습니다.');
                    refreshComments(playlistId);
                },
                error: function(xhr) {
                    console.error('삭제 실패:', xhr.responseText);
                    alert('삭제 실패했습니다.');
                }
            });
        }
    }

    /*
    * 설계 목적: 대댓글의 경우 부모 댓글의 id값을 찾고 그것을 parent_id 칼럼에 넣어야 부모관계가 성립합니다.
    * 각 댓글 밑에 대댓글 입력 폼을 넣은 적도 있는데, 별로 보기에 깔끔하지 않았을 뿐더러,
    * 대댓글 submit을 통해 부모의 정보 전달하는 점에서도 효율적이지 않았기 때문에,
    * 부모 관계 설립을 위해 만들어진 코드입니다.
    *
    * 작동 원리:
    * reply-toggle-btn 클래스를 가진 버튼의 클릭을 감지합니다. 부모의 요소를 여기서 다룹니다.
    * 그 후, closest() 함수로 .reply-form-container을 찾습니다. 여기서는 자식의 요소를 다룹니다.
    * 이후에 display 속성을 변경시켜 폼을 보이게 하거나, 보이지 않게 합니다.
    *
    * */
    if (e.target.classList.contains('reply-toggle-btn')) {
        console.log('Reply toggle clicked');
        e.stopPropagation();

        const container = e.target.closest('.comment-container');
        const formContainer = container?.querySelector('.reply-form-container');

        if (formContainer) {
            formContainer.style.display = formContainer.style.display === 'none' ? 'block' : 'none';
        }
    }

    // 수정 버튼
    if (e.target.classList.contains('comment-update-btn')) {
        console.log('업데이트 버튼 작동');
        e.stopPropagation();

        const commentId = e.target.getAttribute('data-reply-id');
        openCommentUpdatePopup(commentId, playlistId);
    }
}, true);


/*
*
* 작동 원리:
* reply-form 클래스를 가진 폼에 반응합니다.
* 댓글 내용은 json으로 보내고, 부모의 id는 url을 통해 전송합니다.
*
* */

document.addEventListener('submit', function(e) {
    if (e.target.classList.contains('reply-form')) {
        e.preventDefault();

        const playlistId = document.getElementById('playlist-id').value;
        const parentId = e.target.querySelector('.reply-create-btn')?.getAttribute('data-parent-id');
        const content = e.target.querySelector('textarea').value;

        console.log('대댓글창 열렸음');

        if (!content.trim()) {
            alert('대댓글을 입력해주세요');
            return;
        }

        $.ajax({
            url: `/api/playlists/${playlistId}/comments/${parentId}/reply`,
            type: 'POST',
            contentType: 'application/json;',
            dataType: 'json',
            data: JSON.stringify({
                comment: content
            }),
            beforeSend: function(xhr) {
                if (token) {
                    xhr.setRequestHeader('Authorization', 'Bearer ' + token);
                }
            },
            success: function(response) {
                console.log('대댓글 작성 성공:', response);
                alert('대댓글 작성이 완료되었습니다.');
                e.target.querySelector('textarea').value = '';
                refreshComments(playlistId);
            },
            error: function(xhr) {
                console.error('대댓글 작성 실패:', xhr.responseText);
                alert('대댓글 작성에 실패했습니다');
            }
        });
    }
});


/*
*
* 설계 목적:
* 댓글 수정을 눌렀을 때, 그 창에서 바로 댓글이 수정되게 하는 방식이 너무 어려워서 방식을 찾다가 이 방식을 찾았습니다.
*
* 작동 원리:
* AJAX GET 요청으로 서버에서 수정 버튼을 누른 그 댓글의 정보를 가져옵니다.
* 팝업이 뜨고, 응답받은 author(댓글 작성자의 이메일)과 comment(댓글 본문)를 모달의 input/textarea에 불러옵니다.
* layerPop() 함수로 모달을 화면에 가져오고, 버튼을 눌러 updateComment()를 호출할때까지 기다립니다.
*
*/

function openCommentUpdatePopup(id, playlistId) {
    $.ajax({
        url: `/api/playlists/${playlistId}/comments/${id}`,
        type: 'GET',
        dataType: 'JSON',
        beforeSend: function(xhr) {
            if (token) {
                xhr.setRequestHeader('Authorization', 'Bearer ' + token);
            }
        },
        success: function(response) {
            console.log('댓글 조회 성공:', response);
            document.getElementById('modalWriter').value = response.author; // 급하게 수정했는데, 실패시 nickname으로 바꿀 것.
            document.getElementById('modalContent').value = response.comment;
            document.getElementById('modalCommentId').value = id;
            layerPop('commentUpdatePopup');
        },
        error: function(xhr) {
            console.error('댓글 조회 실패:', xhr.responseText);
            alert('댓글 조회 실패');
        }
    });
}

function closeCommentUpdatePopup() {
    console.log('수정 모달 닫기');
    document.getElementById('modalContent').value = '';
    document.getElementById('modalWriter').value = '';
    document.getElementById('modalCommentId').value = '';
    layerPopClose();
}

function updateComment() {
    const articleId = document.getElementById('article-id').value;
    const commentId = document.getElementById('modal-comment-id').value;
    const content = document.getElementById('modal-content').value;

    if (!content || content.trim() === "") {
        alert("공백 또는 입력하지 않은 부분이 있습니다.");
        return;
    }

    $.ajax({
        url: `/api/playlists/${playlistId}/comments/${commentId}`,
        type: 'PATCH',
        contentType: 'application/json;',
        dataType: 'json',
        data: JSON.stringify({
            comment: content
        }),
        beforeSend: function(xhr) {
            if (token) {
                xhr.setRequestHeader('Authorization', 'Bearer ' + token);
            }
        },
        success: function(response) {
            console.log('수정 완료:', response);
            alert('수정되었습니다.');
            closeCommentUpdatePopup();
            refreshComments(articleId);
        },
        error: function(xhr) {
            console.error('수정 실패:', xhr.responseText);
            alert('수정 실패했습니다.');
        }
    });
}


/*
* 작동 원리:
* jQuery 클래스로 popName ID를 가진 요소를 선택합니다.
* 해당 요소가 overlay 클래스 div로 감싸져 있는지 확인하고, 감싸져 있지 않다면 overlay_t로 감쌉니다.
*
* 주의사항:
* 모달의 초기 css는 display: none이여야합니다.
*/

function layerPop(popName) {
    var $layer = $("#" + popName);
    if (!$layer.parent().hasClass('overlay_t')) {
        $layer.wrap('<div class="overlay_t"></div>');
    }
    $layer.fadeIn(500, function() {
        $(this).css('display', 'inline-block');
    });
    $('body').css('overflow', 'hidden');
}

function layerPopClose() {
    $(".popLayer").hide().unwrap('');
    $('body').css('overflow', 'auto');
    $(".popLayer video").each(function() {
        this.pause();
        this.load();
    });
}

/*
* 설계 목적:
* DOM 요소에 접근 가능한 시점에 이벤트 리스너가 초기화되도록 하기 위한 함수입니다.
*
* */

if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', function() {
        console.log('DOM 로딩 완료');
        initializeCommentListeners();
    });
} else {
    console.log('DOM 이미 로딩됨');
    initializeCommentListeners();
}

console.log('DOM 세팅 완료');