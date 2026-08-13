function toggleDropdown(button) {
    const dropdown = button.nextElementSibling;
    
    document.querySelectorAll('.dropdown-menu').forEach(menu => {
        if (menu !== dropdown) menu.classList.add('hidden');
    });

    dropdown.classList.toggle('hidden');
}

window.addEventListener('click', function(e) {
    if (!e.target.closest('.relative')) {
        document.querySelectorAll('.dropdown-menu').forEach(menu => {
            menu.classList.add('hidden');
        });
    }
});

function deletePost(postId) {
    if (confirm("이 게시글을 삭제하시겠습니까?")) {
        fetch(`/community/delete/${postId}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            }
        })
        .then(response => {
            if (response.ok) {
                alert("삭제되었습니다.");
                location.reload(); 
            } else {
                alert("삭제 처리에 실패했습니다.");
            }
        })
        .catch(error => console.error('Error:', error));
    }
}