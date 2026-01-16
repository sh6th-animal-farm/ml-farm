export const UI = {
    show(id) { document.getElementById(id).style.display = 'block'; },
    hide(id) { document.getElementById(id).style.display = 'none'; },
    clear(id) { document.getElementById(id).innerHTML = ''; }
}