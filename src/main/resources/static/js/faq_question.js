document.addEventListener('DOMContentLoaded', function() {
    const typeCards = document.querySelectorAll('.type-card');
    const inquiryTypeInput = document.getElementById('inquiryType');

    typeCards.forEach(card => {
        card.addEventListener('click', function() {
            typeCards.forEach(c => c.classList.remove('active'));
            this.classList.add('active');
            inquiryTypeInput.value = this.getAttribute('data-type');
        });
    });

    const priorityBtns = document.querySelectorAll('.btn-priority');
    const priorityInput = document.getElementById('priority');

    priorityBtns.forEach(btn => {
        btn.addEventListener('click', function() {
            priorityBtns.forEach(b => b.classList.remove('active'));
            this.classList.add('active');
            priorityInput.value = this.getAttribute('data-priority');
        });
    });

    const titleInput = document.getElementById('title');
    const titleCount = document.getElementById('titleCount');
    titleInput.addEventListener('input', function() {
        titleCount.textContent = this.value.length;
    });

    const contentInput = document.getElementById('content');
    const contentCount = document.getElementById('contentCount');
    contentInput.addEventListener('input', function() {
        contentCount.textContent = this.value.length;
    });

    const btnSubmit = document.getElementById('btnSubmit');
    const agreeCheck = document.getElementById('agreeCheck');

    btnSubmit.addEventListener('click', function() {
        if (!titleInput.value.trim()) {
            alert('문의 제목을 입력해주세요.');
            titleInput.focus();
            return;
        }

        if (!contentInput.value.trim()) {
            alert('상세 내용을 입력해주세요.');
            contentInput.focus();
            return;
        }

        if (!agreeCheck.checked) {
            alert('개인정보 수집 및 이용에 동의하셔야 문의 접수가 가능합니다.');
            agreeCheck.focus();
            return;
        }

        const formElement = document.getElementById('inquiryForm');
        const formData = new FormData(formElement);

        fetch('/api/inquiries/save', {
            method: 'POST',
            body: formData
        })
        .then(response => response.json())
        .then(data => {
            if (data.status === 'success') {
                alert('문의가 성공적으로 접수되었습니다.');
                window.location.href = '/faq'; 
            } else {
                alert(data.message || '오류가 발생했습니다.');
                if (data.message === '로그인이 필요합니다.') {
                    window.location.href = '/';
                }
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('서버와 통신 중 에러가 발생했습니다.');
        });
    });
});