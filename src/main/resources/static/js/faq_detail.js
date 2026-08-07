document.addEventListener('DOMContentLoaded', function() {
    const replyForm = document.getElementById('replyForm');

    if (replyForm) {
        replyForm.addEventListener('submit', function(e) {
            e.preventDefault(); 

            const formData = new FormData(replyForm);

            fetch('/api/inquiries/reply', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                },
                body: new URLSearchParams(formData)
            })
            .then(response => response.json())
            .then(data => {
                if (data.status === 'success') {
                    alert(data.message || '답변이 등록되었습니다.');
                    location.reload(); 
                } else {
                    alert(data.message);
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('답변 저장 중 통신 오류가 발생했습니다.');
            });
        });
    }
});