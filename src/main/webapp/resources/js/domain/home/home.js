import { http } from "../../api/http_client.js";

document.addEventListener('DOMContentLoaded', async function() {
    const cards = await http.get(`${ctx}/project/list/fragment/main`);
    let projectArea = document.querySelector(".project-card-list");
    projectArea.innerHTML = cards;

    const chartCtx = document.getElementById('kocChart').getContext('2d');
    new Chart(chartCtx, {
        type : 'line',
        data : {
            labels : [ '1월', '2월', '3월', '4월', '5월', '6월' ],
            datasets : [ {
                label : 'KOC 거래가 (원)',
                data : [ 28500, 29200, 27800, 31000, 33500, 35000 ],
                borderColor : '#4a9f2e',
                backgroundColor : 'rgba(74, 159, 46, 0.1)',
                borderWidth : 3,
                fill : true,
                tension : 0.4,
                pointRadius : 0,
                pointHoverRadius : 6
            } ]
        },
        options : {
            responsive : true,
            maintainAspectRatio : false,
            plugins : {
                legend : {
                    display : false
                }
            },
            scales : {
                y : {
                    beginAtZero : false,
                    grid : {
                        color : '#f0f0f0'
                    },
                    ticks : {
                        font : {
                            size : 11
                        }
                    }
                },
                x : {
                    grid : {
                        display : false
                    },
                    ticks : {
                        font : {
                            size : 11
                        }
                    }
                }
            }
        }
    });
});